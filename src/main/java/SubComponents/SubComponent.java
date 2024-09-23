package SubComponents;

import components.Component;
import editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class  SubComponent{
    public int imguiGroup =0;
    public transient Component owner;
    public void destroy(){

    }
    public void start(){

    }
    public void die(){

    }
    public String RunningGui(int AbilitySize, List<GameObject> activeGameObjects, int ID){
        return null;
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
    public void updateData(HashMap<String, String> data, boolean running){

    }

    public List<GameObject> EditorGui(List<GameObject> activeGameObjects,HashMap<String,String> data) {
        try {
            List<Field[]> farm = new ArrayList<>();
            for(Class cls = this.getClass();
                cls!=null;
                cls = cls.getSuperclass()) {
                farm.add(cls.getDeclaredFields());
            }
            int extend=-1;
            for (Field[] fields:farm) {
                extend++;
                for (Field field : fields) {

                    boolean isPermissible = Modifier.isTransient(field.getModifiers()) || Modifier.isProtected(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
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
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                                                                Class owner = getFieldOwner(change, extend);
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

                        }

                    } else if (type == float.class) {

                        float val = (float) value;

                        float newval = JImGui.dragFloat(name, val);
                        if (val != newval) {
                            for (GameObject go : activeGameObjects) {
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                                                                Class owner = getFieldOwner(change, extend);
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

                        }
                    } else if (type == double.class) {

                        double valll = (double) value;
                        float val = (float) valll;

                        float newval = JImGui.dragFloat(name, val);

                        if (val != newval) {
                            for (GameObject go : activeGameObjects) {
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                                                                Class owner = getFieldOwner(change, extend);
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

                        }
                    } else if (type == boolean.class) {
                        boolean val = (boolean) value;
                        if (ImGui.checkbox(name + ": ", val)) {
                            for (GameObject go : activeGameObjects) {
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                                                                Class owner = getFieldOwner(change, extend);
                                        Field fld = owner.getDeclaredField(name);

                                        fld.set(change, !val);

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
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                        if (!comp.equals(owner)) {
                                                                                    Class owner = getFieldOwner(change, extend);
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
                        }
                    } else if (type == Vector3f.class) {

                        Vector3f val = (Vector3f) value;
                        float[] imVec = {val.x, val.y, val.z};
                        if (ImGui.dragFloat3(name + ": ", imVec)) {
                            for (GameObject go :
                                    activeGameObjects) {
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                        if (!comp.equals(owner)) {
                                            Class owner = getFieldOwner(change, extend);
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
                            }
                            val.set(imVec[0], imVec[1], imVec[2]);
                        }
                    } else if (type == Vector4f.class) {
                        Vector4f val = (Vector4f) value;
                        float[] difference = JImGui.colorPicker4(name, val);
                        if (difference != null) {
                            for (GameObject go :
                                    activeGameObjects) {
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                        if (!comp.equals(owner)) {
                                            Class owner = getFieldOwner(change, extend);
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
                        }
                    } else if (type.isEnum()) {
                        String[] enumValues = getEnumValues(type);
                        String enumType = ((Enum) value).name();
                        ImInt index = new ImInt(indexOf(enumType, enumValues));
                        if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {

                            for (GameObject go : activeGameObjects) {
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
                                    if (change != null) {
                                        Class owner = getFieldOwner(change, extend);
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

                            }
                            field.set(this, type.getEnumConstants()[index.get()]);
                        }
                    } else if (type == String.class) {
                        String val = (String) value;
                        String newVal = JImGui.inputText(field.getName() + ": ",
                                (String) value);
                        if (val != newVal) {
                            for (GameObject go : activeGameObjects) {
                                Component comp = go.getComponent(owner.getClass());
                                if (comp != null) {
                                    SubComponent change = comp.getSubComponent(this.getClass());
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


    public Class getFieldOwner(SubComponent c, int extended){// gets the class that actually owns the field,
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


}
