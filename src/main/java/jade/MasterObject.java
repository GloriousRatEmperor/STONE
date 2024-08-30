package jade;

import SubComponents.SubComponent;
import components.Component;
import editor.JImGui;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MasterObject {
    private GameObject object;
    private List<Component> components=new ArrayList<>();
    private List<SubComponent> subComponents=new ArrayList<>();
    public void addGameObject(GameObject go){
        if(object==null){
            object=go;
        }
        List<Component> comps=go.getAllComponents();
        for (Component comp:comps){
            List<SubComponent> subs=comp.GetAllsubComponents();
            if(subs!=null) {
                for (SubComponent sub : subs) {
                    if (getSubComponent(sub.getClass()) == null) {
                        subComponents.add(sub);
                    }
                }
            }
            if(getComponent(comp.getClass())==null){
                components.add(comp);
            }
        }

    }
    public void Imgui(List<GameObject> activeGameObjects){

    }
    public void EditorImgui(List<GameObject> activeGameObjects){
        try {
            Field[] fields = object.getClass().getDeclaredFields();
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
                Object value = field.get(object);
                String name = field.getName();

                if (type == int.class) {

                    int val = (int) value;

                    int newval = JImGui.dragInt(name, val);
                    if (val != newval) {
                        for (GameObject go : activeGameObjects) {


                            Field fld = go.getClass().getDeclaredField(name);
                            boolean cisPrivate = Modifier.isPrivate(fld.getModifiers());
                            if (cisPrivate) {
                                fld.setAccessible(true);
                            }

                            fld.set(go, (int) fld.get(go) + newval-val);
                            if (cisPrivate) {
                                fld.setAccessible(false);
                            }



                        }
                        field.set(object, newval);

                    }

                } else if (type == float.class) {

                    float val = (float) value;

                    float newval = JImGui.dragFloat(name, val);
                    if (val != newval) {
                        for (GameObject go : activeGameObjects) {


                            Field fld = go.getClass().getDeclaredField(name);
                            boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                            if (cprivate) {
                                fld.setAccessible(true);
                            }

                            fld.set(go, (float) fld.get(go) + newval-val);
                            if (cprivate) {
                                fld.setAccessible(false);
                            }


                        }
                        field.set(object, newval);

                    }
                }else if (type == double.class) {
                    double valll = (double)value;
                    float val = (float) valll;

                    float newval = JImGui.dragFloat(name, val);
                    if (val != newval) {
                        for (GameObject go : activeGameObjects) {


                            Field fld = go.getClass().getDeclaredField(name);
                            boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                            if (cprivate) {
                                fld.setAccessible(true);
                            }

                            fld.set(go, (double) fld.get(go) + newval-val);
                            if (cprivate) {
                                fld.setAccessible(false);
                            }


                        }
                        field.set(object, (double)newval);

                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        for (GameObject go : activeGameObjects) {


                            Field fld = go.getClass().getDeclaredField(name);

                            fld.set(go, !val);


                        }
                        field.set(object, !val);

                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    Vector2f past=new Vector2f(val);
                    JImGui.drawVec2Control(name, val);
                    if(!past.equals(val)){
                        for (GameObject go : activeGameObjects) {
                            Field fld = go.getClass().getDeclaredField(name);
                            boolean cprivate = Modifier.isPrivate(fld.getModifiers());
                            if (cprivate) {
                                fld.setAccessible(true);
                            }
                            Vector2f aval=(Vector2f) fld.get(go);
                            aval.set(aval.x-past.x+val.x,aval.y-past.y+val.y);
                            if (cprivate) {
                                fld.setAccessible(false);
                            }

                        }
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    JImGui.colorPicker4(name, val);
                }  else if (type == String.class) {
                    field.set(object,
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
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                activeGameObjects=c.masterGui(activeGameObjects);
        }
//        for (SubComponent c : subComponents) {
//            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
//                activeGameObjects=c.masterGui(activeGameObjects);
//        }
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
    public <T extends SubComponent> T getSubComponent(Class<T> SubComponentClass) {
        for (SubComponent c : subComponents) {
            if (SubComponentClass.isAssignableFrom(c.getClass())) {
                try {
                    return SubComponentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting SubComponent.";
                }
            }
        }

        return null;
    }
    public void clear(){
        this.object=null;
        this.subComponents.clear();
        this.components.clear();
    }

    public List<Component> getAllComponents() {
        return components;
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
}
