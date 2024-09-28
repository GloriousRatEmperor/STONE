package jade;

import SubComponents.SubComponent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import editor.JImGui;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.AssetPool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameObject {

    protected static int ID_COUNTER = 0;
    transient int uid = -1;

    public String name;
    public int allied=1;
    protected List<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;


    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        if(!Objects.equals(name, "MasterObject")){
            generateUid();
        }

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
        if (!c.isactive) {
            c.begin();
        }
    }
    public void Interact(GameObject target) {
        for (int i=0; i < components.size(); i++) {

            components.get(i).Interact(target);
        }
    }
    public void startMove(Transform target) {
        for (int i=0; i < components.size(); i++) {

            components.get(i).startMove(target);
        }
    }
    public void stopMove() {
        for (int i=0; i < components.size(); i++) {

            components.get(i).stopMove();
        }
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
    public void die() {
        for (int i=0; i < components.size(); i++) {
            components.get(i).die();
        }
        destroy();
    }
    public void runningUpdateDraw() {
        for (int i=0; i < components.size(); i++) {

            components.get(i).runningUpdateDraw();
        }
    }
    public void editorUpdateDraw() {

        for (int i=0; i < components.size(); i++) {
            components.get(i).editorUpdateDraw();
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

    public void EditorStuffImgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();


            for (Field field : fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                boolean isFinal = Modifier.isFinal(field.getModifiers());
                if (isTransient||isFinal) {
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int)value;

                    field.set(this, JImGui.dragInt(name, val));
                } else if (type == float.class) {

                    float val = (float)value;
                    field.set(this, JImGui.dragFloat(name, val));
                } else if (type == double.class) {

                float val = (float)value;
                field.set(this,(double) JImGui.dragFloat(name, val));
            } else if (type == boolean.class) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);

                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f)value;
                    JImGui.drawVec2Control(name, val);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f)value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f)value;
                    JImGui.colorPicker4(name, val);
                }  else if (type == String.class) {
                    field.set(this,
                            JImGui.inputText(field.getName() + ": ",
                                    (String)value));
                }


                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(this.getComponent(SpriteRenderer.class)!=null) {
            Sprite sprite= this.getComponent(SpriteRenderer.class).getSprite();
        }
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.LevelEditorStuffImgui();
        }
    }
    public void RunningGui() {

        if (this.getComponent(SpriteRenderer.class) != null) {
            Sprite sprite = this.getComponent(SpriteRenderer.class).getSprite();
            Vector2f[] texCoords = sprite.getTexCoords();
            imgui.ImGuiIO io = ImGui.getIO();


            ImGui.setNextWindowPos(10, io.getDisplaySizeY() * 3 / 4 + 50);
            if (ImGui.beginChild("mainImage", 200, 200)) {
                ImGui.image(sprite.getTexture().getId(), 200, 200, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y);
                ImGui.endChild();
            }



        }
    }

//        int BuildSize=80;
//        UnitBuilder unitBuilder=this.getComponent(UnitBuilder.class);
//        int columnsBuild=(int)(io.getDisplaySizeX()-250)/BuildSize;
//        if( columnsBuild>0 & unitBuilder!=null) { //in case screen is minimized
//            ImGui.setNextWindowPos(250,io.getDisplaySizeY()*3/4+50);
//            if (ImGui.beginChild("Abilities",  columnsBuild * BuildSize, BuildSize, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
//                if (ImGui.beginTable("null",  columnsBuild, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
//                    int id=1;
////                        for(UNMADE a:unitBuilder.queue) {
////                            id++;
////                            a.Imgui(BuildSize,activeGameObjects,id);
////                        }
//
//                    ImGui.endTable();
//
//                }
//
//            }
//
//            ImGui.endChild();
//
//        }


    public GameObject CopyProperties(GameObject master){

        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {

                boolean isTransient = Modifier.isTransient(field.getModifiers());
                boolean isFinal = Modifier.isFinal(field.getModifiers());
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                boolean isProtected = Modifier.isProtected(field.getModifiers());
                if (isTransient||isFinal||isStatic||isProtected) {
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }
                Object value = field.get(this);

                field.set(master, value);
                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
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
                .registerTypeAdapter(SubComponent.class, new SubComponentDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);



        // dunno why this was a thing?
//        obj.generateUid();
//        for (Component c : obj.getAllComponents()) {
//            c.generateId();
//        }

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
