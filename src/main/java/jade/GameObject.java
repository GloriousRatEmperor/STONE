package jade;

import SubComponents.Ability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;
import util.AssetPool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;

    public String name;
    private List<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();

        this.uid = ID_COUNTER++;
    }


    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {

        for (int i=0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }


    public void addComponent(Component c) {
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i=0; i < components.size(); i++) {

            components.get(i).update(dt);
        }
    }

    public void updateDraw() {
        for (int i=0; i < components.size(); i++) {

            components.get(i).updateDraw();
        }
    }
    public void editorUpdate(float dt) {
        for (int i=0; i < components.size(); i++) {
            components.get(i).editorUpdate(dt);
        }
    }

    public void start() {
        for (int i=0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public void imgui() {
        if(this.getComponent(SpriteRenderer.class)!=null) {
            Sprite sprite= this.getComponent(SpriteRenderer.class).getSprite();
        }
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imgui();
        }
    }
    public List<GameObject> masterGui(List<GameObject> activeGameObjects) {
        if(this.getComponent(SpriteRenderer.class)!=null) {
            Sprite sprite= this.getComponent(SpriteRenderer.class).getSprite();
            Vector2f[] texCoords = sprite.getTexCoords();
            imgui.ImGuiIO io = ImGui.getIO();

            ImGui.setNextWindowPos(10,io.getDisplaySizeY()*3/4+50);
            if(ImGui.beginChild("mainImage",200,200)) {
                ImGui.image(sprite.getTexture().getId(), 200, 200, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y);
            }ImGui.endChild();

            int AbilitySize=80;
            CastAbilities caster=this.getComponent(CastAbilities.class);
            int columns=(int)(io.getDisplaySizeX()-250)/AbilitySize;
            if(columns>0 & caster!=null) { //in case screen is minimized
                ImGui.setNextWindowPos(250,io.getDisplaySizeY()*3/4+50);
                if (ImGui.beginChild("Abilities", columns * AbilitySize, AbilitySize, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                    if (ImGui.beginTable("null", columns, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                        ImGui.tableNextColumn();
                        int id=1;
                        for(Ability a:caster.abilities) {
                            ImGui.pushID(id++);
                            sprite=a.getSprite();
                            texCoords = sprite.getTexCoords();
                            if (ImGui.imageButton(sprite.getTexture().getId(), AbilitySize, AbilitySize, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                                for (GameObject go:activeGameObjects) {
                                    CastAbilities cast= go.getComponent(CastAbilities.class);
                                    if (!(cast ==null)){
                                        cast.castAbility(a.name,MouseListener.getScreen());
                                    }
                                }
                            }ImGui.popID();
                        }

                        ImGui.endTable();

                    }

                }

                ImGui.endChild();
            }


        }
//        for (Component c : components) {
//            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
//                activeGameObjects=c.masterGui(activeGameObjects);
//        }
        return activeGameObjects;
    }
    public List<GameObject> editMasterGui(List<GameObject> activeGameObjects) {
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                activeGameObjects=c.masterGui(activeGameObjects);
        }
        return activeGameObjects;
    }
    public GameObject mengui(GameObject master) {
        for (Component c : components) {
            Component masterComp=master.getComponent(c.getClass());
            if (masterComp==null){
                Component clone=c.Clone();
                Field[] fields = c.getClass().getDeclaredFields();
                try{
                for (Field field : fields) {
                    boolean isTransient = Modifier.isTransient(field.getModifiers());
                    if (isTransient) {
                        continue;
                    }

                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                    if (isPrivate) {
                        field.setAccessible(true);
                    }
                    Object value = field.get(c);
                    field.set(clone, value);
                    if (isPrivate) {
                        field.setAccessible(false);
                    }
                }
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
                master.addComponent(clone);
            }else{
                c.mengui(masterComp);
            }
        }
        return master;
    }

    public void destroy() {

        this.isDead = true;
        for (int i=0; i < components.size(); i++) {
            components.get(i).destroy();
        }
    }

    public GameObject copy() {
        // TODO: come up with cleaner solution
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);

        obj.generateUid();
        for (Component c : obj.getAllComponents()) {
            c.generateId();
        }

        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
        }

        return obj;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public int getUid() {
        return this.uid;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public void generateUid() {
        this.uid = ID_COUNTER++;
    }

    public boolean doSerialization() {
        return this.doSerialization;
    }


}
