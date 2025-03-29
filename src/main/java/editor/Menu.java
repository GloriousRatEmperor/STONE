package editor;

import components.SubComponents.Abilities.Ability;
import components.gamestuff.SpriteRenderer;
import components.unitcapabilities.defaults.CastAbilities;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import jade.GameObject;
import jade.MasterObject;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector2f;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

import static jade.Window.casting;
import static jade.Window.massCast;

public class Menu {
    private List<GameObject> activeGameObjects;
    MasterObject MasterObject=new MasterObject(true);
    public int allied;

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
        this.ids=new ArrayList<>();
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;

    }

    public void imgui() throws NoSuchFieldException {

        if (activeGameObjects.size() > 0 && activeGameObjects.get(0) != null) {
            primairyObject = activeGameObjects.get(0);
            imgui.ImGuiIO io = ImGui.getIO();
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



}
