package jade;

import SubComponents.SubComponent;
import components.Component;
import components.UnitBuilder;
import editor.JImGui;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static util.Img.Color;

public class MasterObject {
    private GameObject object;
    public Boolean isPlaying;
    private HashMap<String, String> guiData =new HashMap<>();
    private List<Component> components=new ArrayList<>();
    private List<UnitBuilder> builders=new ArrayList<>();
    private List<SubComponent> subComponents=new ArrayList<>();
    public MasterObject(Boolean playing){
        isPlaying= playing;
    }
    public void addGameObject(GameObject go){
        if(object==null){
            object=go;
        }
        List<Component> comps=go.getAllComponents();
        for (Component comp:comps){
            List<? extends SubComponent> subs=comp.GetAllsubComponents();
            if(subs!=null) {
                for (SubComponent sub : subs) {
                    if (!containsSubcomponent(sub.getID())) {
                        if(sub.imguiGroup>0||!isPlaying) {
                            subComponents.add(sub);
                        }
                    }
                }
            }
            if(comp.getClass()==UnitBuilder.class&isPlaying) {
                builders.add((UnitBuilder) comp);
            }
            else if(getComponent(comp.getClass())==null){
                    components.add(comp);
            }

        }

        subComponents.sort(Comparator.comparingInt(a -> a.imguiGroup));
        for (Component c:components) {
            c.updateData(guiData,isPlaying);
        }
        for (SubComponent s:subComponents) {
            s.updateData(guiData,isPlaying);
        }
    }
    public void RunningGui(List<GameObject> activeGameObjects){
        object.RunningGui();
//        for (Component comp: components){
//            comp.Gui();
//        }
        String desc=null;
        String descmb=null;
        if(!builders.isEmpty()){

            imgui.ImGuiIO io = ImGui.getIO();
            int buildSize = 60;
            int columns = (int) (io.getDisplaySizeX() - 250) / buildSize;
            if (columns > 0) { //in case screen is minimized

                ImGui.setNextWindowPos(250, io.getDisplaySizeY() -buildSize*2);
                int index=0;
                if (ImGui.beginChild("Builds", columns * buildSize, buildSize, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                    if (ImGui.beginTable("nulls", columns, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                        int id = 1;
                        while (index < builders.size()) {
                            UnitBuilder a = builders.get((index));
                            id++;
                            descmb= a.RunningGui(buildSize, activeGameObjects, id);
                            index++;
                            if (descmb != null) {
                                desc = descmb;
                            }

                        }

                        ImGui.endTable();

                    }
                    ImGui.endChild();
                }
            }

        }
        if(!subComponents.isEmpty()) {
            descmb= imguiSubComponents(activeGameObjects);
            if(descmb!=null){
                desc=descmb;
            }
        }
        ImGui.end();
        if(desc!=null){
            imguiDescription(desc);
        }

    }
    public String imguiSubComponents(List<GameObject> activeGameObjects) {
        int nextImguiGroup = subComponents.get(0).imguiGroup;
        int index = 0;
        String desc = null;
        int effectSpace=250;
        if (nextImguiGroup == 1) {
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            imgui.ImGuiIO io = ImGui.getIO();
            int AbilitySize = 80;
            int columns = (int) (io.getDisplaySizeX() - 250-effectSpace) / AbilitySize;
            if (columns > 0) { //in case screen is minimized

                ImGui.setNextWindowPos(250, io.getDisplaySizeY() * 3 / 4 + 50);
                String descmb;
                if (ImGui.beginChild("Abilities", columns * AbilitySize, AbilitySize, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                    if (ImGui.beginTable("null", columns, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                        int id = 1;
                        while (index < subComponents.size()) {
                            SubComponent a = subComponents.get((index));
                            if (a.imguiGroup == 1) {
                                id++;
                                descmb = a.RunningGui(AbilitySize, activeGameObjects, id);
                                if (descmb != null) {
                                    desc = descmb;
                                }
                            } else {
                                nextImguiGroup = a.imguiGroup;
                                break;
                            }
                            index++;

                        }

                        ImGui.endTable();

                    }
                    ImGui.endChild();
                }
            }


            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            //////////////////////////////IMGUI ABILITIES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        }

        if (nextImguiGroup == 2) {
            imgui.ImGuiIO io = ImGui.getIO();
            int EffectSize = 35;
            int columns = (int) effectSpace / EffectSize;
            if (columns > 0) { //in case screen is minimized

                ImGui.setNextWindowPos(io.getDisplaySizeX() - effectSpace, io.getDisplaySizeY() * 3 / 4 + 50);
                String descmb;
                if (ImGui.beginChild("Effects", columns * EffectSize, EffectSize, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                    if (ImGui.beginTable("null", columns, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                        int id = 1;
                        while (index < subComponents.size()) {
                            SubComponent a = subComponents.get((index));
                            if (a.imguiGroup == 2) {
                                id++;
                                descmb = a.RunningGui(EffectSize, activeGameObjects, id);
                                if (descmb != null) {
                                    desc = descmb;
                                }
                            } else {
                                nextImguiGroup = a.imguiGroup;
                                break;
                            }
                            index++;

                        }

                        ImGui.endTable();

                    }
                    ImGui.endChild();
                }
            }


        }
        return desc;
    }
    public void imguiDescription(String desc){

        int width=600;
        int height=7000;
            float mouseX=ImGui.getMousePosX();
            float mouseY=ImGui.getMousePosY();
            ImVec2 size=new ImVec2();
            String[] displaytexts=desc.split("\\|");
            int color;
            float wide=0;
            float high=0;
            ImGui.pushStyleColor(ImGuiCol.Text,Color(255,255,255,255));
            for (String str:displaytexts) {

                ImGui.calcTextSize(size, str, false,width);
                if (wide < size.x) {
                    wide = size.x;
                }
                high += size.y;

            }
            ImGui.popStyleColor();
            if(mouseX>-10+ImGui.getIO().getDisplaySizeX()-Math.min(width,wide+20)){
                mouseX=-10+ImGui.getIO().getDisplaySizeX()-Math.min(width,wide+20);

            }
            if(mouseY>ImGui.getIO().getDisplaySizeY()-20-Math.min(height,high)){
                mouseY=ImGui.getIO().getDisplaySizeY()-20-Math.min(height,high);
                high+=20;
            }
            ImGui.setNextWindowPos(mouseX,mouseY-20);     //these ugly twenties are weird but necessary? they come from shit like window padding and my not enough understanding of imgui magic
            ImGui.setNextWindowSize(Math.min(wide+20,width),high+20);
            if(desc!=null){
                if(ImGui.begin("Desc", new ImBoolean(true), ImGuiWindowFlags.NoMouseInputs|ImGuiWindowFlags.NoTitleBar|ImGuiWindowFlags.NoDecoration)) {

                    ImGui.getWindowDrawList().addRectFilled(mouseX, mouseY,
                            mouseX + wide,
                            mouseY + high, Color(0, 0, 0, 255), 5);

                    for (String str:displaytexts){
                        switch (str.charAt(0)){
                            case ('0')->
                                color=Color(220,220,220,255);
                            case ('1') ->
                                    color=Color(255,0,0,255);
                            case('2')->
                                    color=Color(0,255,0,255);
                            case('3')->
                                    color=Color(0,0,255,255);
                            case('4')->
                                    color=Color(70,70,255,255);
                            case('5')->
                                    color=Color(90,90,90,255);
                            case('6')->
                                    color=Color(255,220,20,255); //yellow
                            case('7')->
                                color=Color(225,80,0,255);//orange
                            default -> color=Color(255,255,255,255);


                        }
                        if(color!=Color(255,255,255,255)){
                            str=str.substring(1);
                        }
                        ImGui.pushStyleColor(ImGuiCol.Text,color);

                        ImGui.textWrapped(str);

                        ImGui.popStyleColor();


                    }
                    ImGui.end();
                }



            }

    }



    public void EditorGui(List<GameObject> activeGameObjects){
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
                activeGameObjects=c.EditorGui(activeGameObjects,guiData);
        }
        for (SubComponent c : subComponents) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                activeGameObjects=c.EditorGui(activeGameObjects,guiData);
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
    public Boolean containsSubcomponent(int id) {
        for (SubComponent c : subComponents) {
            if(c.getID()==id) {
                return true;
            }
        }
        return false;
    }
    public void clear(){
        this.object=null;
        builders.clear();
        this.subComponents.clear();
        this.components.clear();
        guiData.clear();
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
