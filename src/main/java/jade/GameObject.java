package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.Sprite;
import components.SpriteRenderer;
import imgui.ImGui;
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
            ImGui.image(sprite.getTexture().getId(), 200, 200,texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y);
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
            if (master.getComponent(c.getClass())==null){
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
