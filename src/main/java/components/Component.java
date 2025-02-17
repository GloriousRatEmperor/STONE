package components;

import components.SubComponents.SubComponent;
import editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import jade.Transform;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static jade.Window.get;
import static jade.Window.getScene;

public abstract class Component {
    private static int ID_COUNTER = 0;
    private int uid = -1;
    public transient boolean isactive=false;

    public transient GameObject gameObject = null;
    protected transient List<? extends SubComponent> subComponents;
    public List<? extends SubComponent> GetAllsubComponents(){
        return this.subComponents;
    }
    public void addSubComponent(SubComponent c){
        System.out.println("ERROR, this class doesn't support subcomponents");
    }
    public List<? extends SubComponent>  getSubComponents(){
        return subComponents;
    }
    public <T extends SubComponent> T getSubComponent(Class<T> subComponentClass) {
        if(subComponents!=null) {
            for (SubComponent c : subComponents) {
                if (subComponentClass.isAssignableFrom(c.getClass())) {
                    try {
                        return subComponentClass.cast(c);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        assert false : "Error: Casting component.";
                    }
                }
            }
        }

        return null;
    }

    public void updateDraw(){

    }
    public void copyProperties(Component mastercComponent){

    }
    public void Interact(GameObject target) {

    }
    public void die() {
        if(subComponents!=null) {
            for (int i = 0; i < subComponents.size(); i++) {

                subComponents.get(i).die();
            }
        }
    }
    public void start() {
        if(this.subComponents!=null) {
            for (int i = 0; i < subComponents.size(); i++) {
                subComponents.get(i).start();
            }
        }
    }
    public void startMove(Transform target) {

    }


    public void update(float dt) {

    }

    public void editorUpdate(float dt) {

    }
    public void editorUpdateDraw() {

    }
    public void runningUpdateDraw() {

    }
    public void updateData(HashMap<String, String> data, boolean running){

    }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }
    public String RunningGui(int Size, List<GameObject> activeGameObjects, int ID){

        return null;
    }

    public List<GameObject> EditorGui(List<GameObject> activeGameObjects,HashMap<String,String> guiData) {
        try {
            List<Field[]> farm = new ArrayList<>();
            for(Class cls = this.getClass();
                cls!=null;
                cls = cls.getSuperclass()) {
                farm.add(cls.getDeclaredFields());
            }
            int extend=-1;
            for (Field[] fields:farm){
                extend++;
                for (Field field : fields) {

                    boolean isPermissible = Modifier.isTransient(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
                    if (isPermissible) {
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

                        int val = (int) value;

                        int newval = JImGui.dragInt(name, val);
                        if (val != newval) {
                            for (GameObject go : activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    Class owner = getFieldOwner(change,extend);
                                    Field fld = owner.getDeclaredField(name);
                                    boolean cisPrivate = Modifier.isPrivate(fld.getModifiers());
                                    if (cisPrivate) {
                                        fld.setAccessible(true);
                                    }

                                    fld.set(change, (int) fld.get(change) + newval - val);

                                    if (cisPrivate) {
                                        fld.setAccessible(false);
                                    }

                                }
                            }

                        }

                    } else if (type == float.class) {

                        float val = (float) value;

                        float newval = JImGui.dragFloat(name, val);
                        if (val != newval) {
                            for (GameObject go : activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    Class owner = getFieldOwner(change,extend);
                                    Field fld = owner.getDeclaredField(name);
                                    boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                                    if (cprivate) {
                                        fld.setAccessible(true);
                                    }

                                    fld.set(change, (float) fld.get(change) + newval - val);
                                    if (cprivate) {
                                        fld.setAccessible(false);
                                    }

                                }
                            }

                        }
                    } else if (type == double.class) {

                        double valll = (double) value;
                        float val = (float) valll;

                        float newval = JImGui.dragFloat(name, val);

                        if (val != newval) {
                            for (GameObject go : activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    Class owner = getFieldOwner(change,extend);
                                    Field fld = owner.getDeclaredField(name);
                                    boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                                    if (cprivate) {
                                        fld.setAccessible(true);
                                    }

                                    fld.set(change, (double) fld.get(change) + newval - val);
                                    if (cprivate) {
                                        fld.setAccessible(false);
                                    }


                                }
                            }

                        }
                    } else if (type == boolean.class) {
                        boolean val = (boolean) value;
                        if (ImGui.checkbox(name + ": ", val)) {
                            for (GameObject go : activeGameObjects) {
                                Component change=go.getComponent(this.getClass());
                                if (change != null) {
                                    Class owner = getFieldOwner(change,extend);
                                    Field fld = owner.getDeclaredField(name);
                                    boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                                    if (cprivate) {
                                        fld.setAccessible(true);
                                    }
                                    fld.set(change, !val);
                                    if (cprivate) {
                                        fld.setAccessible(false);
                                    }

                                }
                            }

                        }
                    } else if (type == Vector2f.class) {
                        Vector2f val = (Vector2f) value;
                        Vector2f past = new Vector2f(val);
                        JImGui.drawVec2Control(name, val);
                        if (!past.equals(val)) {
                            for (GameObject go : activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    if (!change.equals(this)) {
                                        Class owner = getFieldOwner(change,extend);
                                        Field fld = owner.getDeclaredField(name);

                                        boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                                        if (cprivate) {
                                            fld.setAccessible(true);
                                        }
                                        Vector2f aval = (Vector2f) fld.get(change);
                                        aval.set(aval.x - past.x + val.x, aval.y - past.y + val.y);
                                        if (cprivate) {
                                            fld.setAccessible(false);
                                        }
                                    }
                                }

                            }
                        }
                    } else if (type == Vector3f.class) {

                        Vector3f val = (Vector3f) value;
                        float[] imVec = {val.x, val.y, val.z};
                        if (ImGui.dragFloat3(name + ": ", imVec)) {
                            for (GameObject go :
                                    activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    if (!change.equals(this)) {
                                        Class owner = getFieldOwner(change,extend);
                                        Field fld = owner.getDeclaredField(name);
                                        boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                                        if (cprivate) {
                                            fld.setAccessible(true);
                                        }
                                        Vector3f aval = (Vector3f) fld.get(change);
                                        aval.set(aval.x + imVec[0] - val.x, aval.y + imVec[1] - val.y, aval.z - val.z + imVec[2]);
                                        if (cprivate) {
                                            fld.setAccessible(false);
                                        }
                                    }
                                }
                            }
                            val.set(imVec[0], imVec[1], imVec[2]);
                        }
                    } else if (type == Vector4f.class) {
                        Vector4f val = (Vector4f) value;
                        float[] difference = JImGui.colorPicker4(name, val);
                        if (difference != null) {
                            for (GameObject go :
                                    activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    if (!change.equals(this)) {
                                        Class owner = getFieldOwner(change,extend);
                                        Field fld = owner.getDeclaredField(name);
                                        boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                                        if (cprivate) {
                                            fld.setAccessible(true);
                                        }
                                        Vector4f aval = (Vector4f) fld.get(change);
                                        aval.add(difference[0], difference[1], difference[2], difference[3]);
                                        if (cprivate) {
                                            fld.setAccessible(false);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (type.isEnum()) {
                        String[] enumValues = getEnumValues(type);
                        String enumType = ((Enum) value).name();
                        ImInt index = new ImInt(indexOf(enumType, enumValues));
                        if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {

                            for (GameObject go : activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    Class owner = getFieldOwner(change,extend);
                                    Field fld = owner.getDeclaredField(name);
                                    if (isPrivate) {
                                        fld.setAccessible(true);
                                    }
                                    fld.set(change, type.getEnumConstants()[index.get()]);
                                    if (isPrivate) {
                                        fld.setAccessible(false);
                                    }


                                }

                            }
                            field.set(this, type.getEnumConstants()[index.get()]);
                        }
                    } else if (type == String.class) {
                        String val=(String) value;
                        String newVal=JImGui.inputText(field.getName() + ": ",
                                (String) value);
                        if(val!=value) {
                            for (GameObject go : activeGameObjects) {
                                Component change = go.getComponent(this.getClass());
                                if (change != null) {
                                    Class owner = getFieldOwner(change, extend);
                                    Field fld = owner.getDeclaredField(name);
                                    if (isPrivate) {
                                        fld.setAccessible(true);
                                    }
                                    fld.set(change, newVal);
                                    if (isPrivate) {
                                        fld.setAccessible(false);
                                    }


                                }

                            }
                        }
                    }


                    if (isPrivate) {
                        field.setAccessible(false);
                    }
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
        return activeGameObjects;
    }

    public Class getFieldOwner(Component c, int extended){// gets the class that actually owns the field,
        // (which can also be a parent class so that you can change fields of Component.class of a CastAbilities),
        // but primairly added for something like Heal>Ability.
        if(c!=null) {
            Class cla = c.getClass();
            while (extended > 0) {
                cla = cla.getSuperclass();
                extended--;
            }
            return cla;
        }
        return null;
    }
    public void LevelEditorStuffImgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();


            for (Field field : fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) {
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
                    if(name.equals("placeAllied")){
                        int newVal=JImGui.dragInt(name, val);
                        if(val!=newVal){
                            get().allied=newVal;
                            getScene().getLevelEditorSceneInitiallizer().setAllied(newVal);
                            field.set(this, newVal);
                        }
                    }else {

                        field.set(this, JImGui.dragInt(name, val));
                    }
                } else if (type == float.class) {

                    float val = (float)value;
                    field.set(this, JImGui.dragFloat(name, val));
                }else if (type == double.class) {

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
                } else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum)value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                } else if (type == String.class) {
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
    }

    public void generateId() {
        if (this.uid == -1) {
            this.uid = ID_COUNTER++;
        }
    }

    protected  <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValue : enumType.getEnumConstants()) {
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }

    private int indexOf(String str, String[] arr) {
        for (int i=0; i < arr.length; i++) {
            if (str.equals(arr[i])) {
                return i;
            }
        }

        return -1;
    }

    public void destroy() {

    }

    public int getUid() {
        return this.uid;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }
    public void begin(){

        isactive=true;
    }
}
