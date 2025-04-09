package editor;

import components.SubComponents.Abilities.Ability;
import components.gamestuff.Message;
import components.gamestuff.SpriteRenderer;
import components.unitcapabilities.defaults.CastAbilities;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import jade.GameObject;
import jade.MasterObject;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

import static jade.Window.*;

public class Menu {
    private List<GameObject> activeGameObjects;
    private static List<Message> messages=new ArrayList<>();
    MasterObject MasterObject=new MasterObject(true);
    public int allied;
    private String typingString="";
    private GameObject primairyObject = null;
    private List<Integer> ids;
    private Vector4f defautcolor=new Vector4f(0,1,1,1);
    private PickingTexture pickingTexture;
    public static void addMessage(Message message){
        messages.add(message);
    }
//    private boolean IsItemActiveLastFrame()
//    {
//        ImGui.beginPopupContextWindow();
//        if (g.ActiveIdPreviousFrame)
//            return g.ActiveIdPreviousFrame == g.CurrentWindow->DC.LastItemId;
//        return false;
//    }
//
//    private boolean IsItemJustMadeInactive()
//    {
//        return IsItemActiveLastFrame() && !ImGui.isItemActive();
//    }

    public Menu(PickingTexture pickingTexture) {
        this.allied= get().allied;
        this.ids=new ArrayList<>();
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;

    }

    public void imgui() throws NoSuchFieldException {
        imgui.ImGuiIO io = ImGui.getIO();
        if (activeGameObjects.size() > 0 && activeGameObjects.get(0) != null) {
            primairyObject = activeGameObjects.get(0);
            ImGui.setNextWindowSize(io.getDisplaySizeX(),io.getDisplaySizeY()/4);
            ImGui.setNextWindowPos(0,io.getDisplaySizeY()*3/4);
            ImGui.begin("Menu" , ImGuiWindowFlags.MenuBar| ImGuiWindowFlags.NoResize
                    | ImGuiWindowFlags.NoTitleBar| ImGuiWindowFlags.NoCollapse| ImGuiWindowFlags.NoDecoration|ImGuiWindowFlags.NoBringToFrontOnFocus);

            ///this is how (and where) shit would be presented here for button options of the right click
//            if (ImGui.menuItem("Add Circle Collider")) {
//                if (activeGameObject.getComponent(CircleCollider.class) == null){
//                    if (activeGameObject.getComponent(Box2DCollider.class) != null) {
//                        activeGameObject.removeComponent(Box2DCollider.class);
//                    }
//                    activeGameObject.addComponent(new CircleCollider());
//                }
//            }
            activeGameObjects.removeIf(GameObject::isDead);
            if(casting){
                Vector2f pos= MouseListener.getWorld();
                for (GameObject go:
                     activeGameObjects) {

                    CastAbilities cast=go.getComponent(CastAbilities.class);
                    if(cast!=null){
                        Ability ability=cast.getAbility(Window.targetAbility);
                        if(ability!=null){
                            ability.castGui(pos, go.transform.position);
                            if(!massCast()){ //TODO move casting process from window to imguilayer?
                                break;
                            }
                        }
                    }

                }
            }

            MasterObject.RunningGui(activeGameObjects);


        }else {
            ImGui.setNextWindowSize(io.getDisplaySizeX(), io.getDisplaySizeY() / 4);
            ImGui.setNextWindowPos(0, io.getDisplaySizeY() * 3 / 4);

            if(ImGui.begin("sender",ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoResize
                    | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse|ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBringToFrontOnFocus)){
                String newVal = JImGui.inputText("send message: ",
                        typingString);
                typingString = newVal;

            }
            if(ImGui.beginChild("writer",io.getDisplaySizeX(), io.getDisplaySizeY() / 4-io.getDisplaySizeY() / 32,true,ImGuiWindowFlags.NoBringToFrontOnFocus)){
                for (Message msg : messages) {
                    ImGui.pushStyleColor(ImGuiCol.Text, msg.color);
                    ImGui.textWrapped(msg.sender + ": " + msg.msg);
                    ImGui.popStyleColor();
                }
                ImGui.pushStyleColor(ImGuiCol.Text, util.Img.color(120,120,120,255));
                ImGui.textWrapped("\n ");
                ImGui.popStyleColor();

            }ImGui.endChild();
            ImGui.end();
            ImGui.setNextWindowSize(io.getDisplaySizeX()/2, io.getDisplaySizeY() / 15);
            ImGui.setNextWindowPos(io.getDisplaySizeX()/2, io.getDisplaySizeY() * 3 / 4);
            if(ImGui.begin("colormessage",ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoResize
                    | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse|ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBringToFrontOnFocus)){
                Vector4f col=defautcolor;
                JImGui.colorPicker4("color",defautcolor);


            }ImGui.end();



        }
    }

    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void clearSelected() {
        this.MasterObject.clear();
        this.ids=new ArrayList<>();
        for (GameObject go : activeGameObjects) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.addColor(0,-0.5f,0,0);
            }
        }
        this.activeGameObjects.clear();
    }

    public void setActiveGameObject(GameObject go) {

        if (go != null) {
            clearSelected();
            addActiveGameObject(go);
            ids.add(go.getUid());
        }

    }

    public void addActiveGameObject(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null ) {
            spr.addColor(0,0.5f,0,0);
        }
        this.activeGameObjects.add(go);
        MasterObject.addGameObject(go);
        ids.add(go.getUid());

    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }

    public List<Integer> getIds(){return ids;}

    public void sendMessage(){
        if(!typingString.equals("")){
            Window.sendMessage(typingString,(int)(defautcolor.x*255),(int)(defautcolor.y*255),(int)(defautcolor.z*255),(int)(defautcolor.w*255));
            typingString="";
        }
    }

}
