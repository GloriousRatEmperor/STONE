package editor;

import components.SpriteRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import jade.GameObject;
import jade.Window;
import org.joml.Vector4f;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;
public class Menu {
    private List<GameObject> activeGameObjects;
    GameObject MasterObject;
    public int allied;
    private List<Vector4f> activeGameObjectsOgColor;
    private GameObject primairyObject = null;
    private List<Integer> ids;
    private PickingTexture pickingTexture;
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
        this.allied= Window.get().allied;
        this.MasterObject=new GameObject("MasterObject");
        this.ids=new ArrayList<>();
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectsOgColor = new ArrayList<>();
    }

    public void imgui() throws NoSuchFieldException {

        if (activeGameObjects.size() > 0 && activeGameObjects.get(0) != null) {
            primairyObject = activeGameObjects.get(0);
            imgui.ImGuiIO io = ImGui.getIO();
            ImGui.setNextWindowSize(io.getDisplaySizeX(),io.getDisplaySizeY()/4);
            ImGui.setNextWindowPos(0,io.getDisplaySizeY()*3/4);
            ImGui.begin("Menu" , ImGuiWindowFlags.MenuBar| ImGuiWindowFlags.NoResize
                    | ImGuiWindowFlags.NoTitleBar| ImGuiWindowFlags.NoCollapse| ImGuiWindowFlags.NoDecoration);

            ///this is how (and where) shit would be presented here for button options of the right click
//            if (ImGui.menuItem("Add Circle Collider")) {
//                if (activeGameObject.getComponent(CircleCollider.class) == null){
//                    if (activeGameObject.getComponent(Box2DCollider.class) != null) {
//                        activeGameObject.removeComponent(Box2DCollider.class);
//                    }
//                    activeGameObject.addComponent(new CircleCollider());
//                }
//            }
            for (GameObject go : activeGameObjects) {
                if(go.isDead()){
                    activeGameObjects.remove(activeGameObjects);
                }
            }
            MasterObject.masterGui(activeGameObjects);

            ImGui.end();

        }
    }

    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void clearSelected() {
        this.MasterObject=new GameObject("MasterObject");
        this.ids=new ArrayList<>();
        if (activeGameObjectsOgColor.size() > 0) {
            int i = 0;
            for (GameObject go : activeGameObjects) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null) {

                    spr.setColorVec(activeGameObjectsOgColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeGameObjectsOgColor.clear();
    }

    public void setActiveGameObject(GameObject go) {

        if (go != null) {
            clearSelected();
            this.activeGameObjects.add(go);
            MasterObject=go.mengui(MasterObject);
            ids.add(go.getUid());
        }

    }

    public void addActiveGameObject(GameObject go) {
//        Mortal m=go.getComponent(Mortal.class);
//        if(m!=null){
//            if(m.allied!=allied){
//                return;
//            }
//        }
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null ) {
            this.activeGameObjectsOgColor.add(new Vector4f(spr.getColor()));
            spr.setColor(8f, 0.8f, 0.0f, 0.8f);
        } else {
            this.activeGameObjectsOgColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
        MasterObject=go.mengui(MasterObject);
        ids.add(go.getUid());

    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }

    public List<Integer> getIds(){return ids;}



}
