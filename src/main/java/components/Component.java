package components;

import editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public abstract class Component {
    private static int ID_COUNTER = 0;
    private int uid = -1;
    public boolean isactive=false;

    public transient GameObject gameObject = null;

    public Component Clone(){
        throw new RuntimeException("you fucktart cloning dumbly dumass"+this.getClass()+" is the class you clonin and it has no clown function STUPID");
    }
    public void updateDraw(){

    }
    public void mengui(Component mastercComponent){

    }
    public void start() {

    }

    public void update(float dt) {

    }

    public void editorUpdate(float dt) {

    }
    public void editorUpdateDraw() {

    }
    public void runningUpdateDraw() {

    }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }


    public List<GameObject>  masterGui(List<GameObject> activeGameObjects) {
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

                    int val = (int) value;

                    int newval = JImGui.dragInt(name, val);
                    if (val != newval) {
                        for (GameObject go : activeGameObjects) {
                            Component change = go.getComponent(this.getClass());
                            if(change!=null) {
                                Field fld = change.getClass().getDeclaredField(name);
                                boolean cisPrivate = Modifier.isPrivate(fld.getModifiers());
                                if (cisPrivate) {
                                    fld.setAccessible(true);
                                }

                                fld.set(change, (int) fld.get(this) + newval-val);

                                if (cisPrivate) {
                                    fld.setAccessible(false);
                                }

                            }
                        }
                        field.set(this, newval);

                    }

                } else if (type == float.class) {

                    float val = (float) value;

                    float newval = JImGui.dragFloat(name, val);
                    if (val != newval) {
                        for (GameObject go : activeGameObjects) {
                            Component change = go.getComponent(this.getClass());
                            if(change!=null) {
                                Field fld = change.getClass().getDeclaredField(name);
                                boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                                if (cprivate) {
                                    fld.setAccessible(true);
                                }

                                fld.set(change, (float) fld.get(this) + newval-val);
                                if (cprivate) {
                                    fld.setAccessible(false);
                                }

                            }
                        }
                        field.set(this, newval);

                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        for (GameObject go : activeGameObjects) {
                            Component change = go.getComponent(this.getClass());
                            if(change!=null) {
                                Field fld = change.getClass().getDeclaredField(name);

                                fld.set(change, !val);
                            }

                        }
                        field.set(this, !val);

                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    JImGui.drawVec2Control(name, val);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    JImGui.colorPicker4(name, val);
                } else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum) value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {

                        for (GameObject go : activeGameObjects) {
                            Component change = go.getComponent(this.getClass());
                            if (change != null) {
                                Field fld = change.getClass().getDeclaredField(name);
                                if (isPrivate) {
                                    fld.setAccessible(true);
                                }
                                fld.set(change,type.getEnumConstants()[index.get()]);
                                if (isPrivate) {
                                    fld.setAccessible(false);
                                }



                            }

                        }
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                } else if (type == String.class) {
                    field.set(this,
                            JImGui.inputText(field.getName() + ": ",
                                    (String) value));
                }


                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
        return activeGameObjects;
    }
    public void  imgui() {
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

                    field.set(this, JImGui.dragInt(name, val));
                } else if (type == float.class) {

                    float val = (float)value;
                    field.set(this, JImGui.dragFloat(name, val));
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
