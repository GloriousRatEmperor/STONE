package jade;

import SubComponents.Abilities.Ability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import editor.JImGui;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import util.AssetPool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jade.Window.getWidth;

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

    public void imgui() {
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
                c.imgui();
        }
    }
    public static int Color(int r, int g, int b, int a) { int ret = a; ret <<= 8; ret += b; ret <<= 8; ret += g; ret <<= 8; ret += r; return ret; }
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
                String desc=null;
                String descmb;
                if (ImGui.beginChild("Abilities", columns * AbilitySize, AbilitySize, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                    if (ImGui.beginTable("null", columns, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                        int id=1;
                        for(Ability a:caster.abilities) {
                            id++;
                            descmb=a.Imgui(AbilitySize,activeGameObjects,id);

                            if(descmb!=null){
                                desc=descmb;
                            }

                        }

                        ImGui.endTable();
                        ImGui.setNextWindowPos(ImGui.getMousePosX(),ImGui.getMousePosY());



                    }

                }

                ImGui.endChild();
                int width=600;
                float mouseX=ImGui.getMousePosX()+10;
                float mouseY=ImGui.getMousePosY()-10;
                if(mouseX>getWidth()-width){
                    mouseX=getWidth()-width;
                }
                ImGui.setNextWindowPos(mouseX,mouseY);
                if(desc!=null){
                    if(ImGui.beginChild("Desc",width,700,false, ImGuiWindowFlags.NoMouseInputs|ImGuiWindowFlags.NoTitleBar|ImGuiWindowFlags.NoDecoration)) {

                        ImVec2 size=new ImVec2();
                        String[] displaytexts=desc.split("\\|");
                        int color;
                        float wide=0;
                        float high=0;
                        for (String str:displaytexts) {

                            ImGui.calcTextSize(size, str, width);
                            if (wide < size.x) {
                                wide = size.x;
                            }
                            high += size.y;
                            ImGui.getWindowDrawList().addRectFilled(mouseX, mouseY,
                                    mouseX + wide,
                                    mouseY + high, Color(0, 0, 0, 255), 5);
                        }
                        for (String str:displaytexts){
                            switch (str.charAt(0)){
                                case ('1') ->
                                        color=Color(255,0,0,255);
                                case('2')->
                                        color=Color(0,255,0,255);
                                case('3')->
                                        color=Color(0,0,255,255);
                                case('4')->
                                        color=Color(70,70,255,255);

                                default -> color=Color(255,255,255,255);


                            }
                            if(color!=Color(255,255,255,255)){
                                str=str.substring(1);
                            }
                            ImGui.pushStyleColor(ImGuiCol.Text,color);

                            ImGui.textWrapped(str);

                            ImGui.popStyleColor();


                        }

                    }ImGui.endChild();


                }

            }

            int BuildSize=80;
            UnitBuilder unitBuilder=this.getComponent(UnitBuilder.class);
            int columnsBuild=(int)(io.getDisplaySizeX()-250)/BuildSize;
            if( columnsBuild>0 & unitBuilder!=null) { //in case screen is minimized
                ImGui.setNextWindowPos(250,io.getDisplaySizeY()*3/4+50);
                if (ImGui.beginChild("Abilities",  columnsBuild * BuildSize, BuildSize, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                    if (ImGui.beginTable("null",  columnsBuild, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize)) {
                        int id=1;
//                        for(UNMADE a:unitBuilder.queue) {
//                            id++;
//                            a.Imgui(BuildSize,activeGameObjects,id);
//                        }

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
                        field.set(this, newval);

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
                        field.set(this, newval);

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
                        field.set(this, (double)newval);

                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        for (GameObject go : activeGameObjects) {


                                Field fld = go.getClass().getDeclaredField(name);

                                fld.set(go, !val);


                        }
                        field.set(this, !val);

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
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                activeGameObjects=c.masterGui(activeGameObjects);
        }
        return activeGameObjects;
    }
    public GameObject copyMissingComponents(GameObject master) {

        for (Component c : components) {
            Component masterComp=master.getComponent(c.getClass());
            if (masterComp==null){
                Component clone=c.Clone();

                Field[] fields = c.getClass().getDeclaredFields();
                try{
                for (Field field : fields) {

                    boolean isTransient = Modifier.isTransient(field.getModifiers());
                    boolean isFinal = Modifier.isFinal(field.getModifiers());
                    boolean isProtected = Modifier.isProtected(field.getModifiers());
                    if (isTransient||isFinal||isProtected) {
                        continue;
                    }

                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                    if (isPrivate) {
                        field.setAccessible(true);
                    }
                    Object value = field.get(c);


                    switch (field.getType().getSimpleName()){

                        case("Vector2f")->
                            value=new Vector2f(((Vector2f) value));
                        case("Vector3f")->
                            value=new Vector3f(((Vector3f) value));
                        case("Vector4f")->
                            value=new Vector4f(((Vector4f) value));
                        case ("Sprite")->
                            value =((Sprite)value).clone();
                        case "float","int","boolean","Boolean","BodyType" -> {
                        }


                        default -> {
                            System.out.println(field.getType().getSimpleName()+" is a new type for copy, plz add it to the known types so that it gets cloned or ignored or whatever");
                        }




                    }


                    field.set(clone, value);
                    if (isPrivate) {
                        field.setAccessible(false);
                    }
                }
                } catch (IllegalAccessException ex) {
                    System.out.println(c.getClass());

                    throw new RuntimeException(ex);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                master.addComponent(clone);
            }else{
                c.copyProperties(masterComp);
            }
        }
        return master;
    }
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
                .registerTypeAdapter(Ability.class, new AbilityDeserializer())
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
