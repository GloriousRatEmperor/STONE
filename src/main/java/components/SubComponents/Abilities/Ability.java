package components.SubComponents.Abilities;

import components.SubComponents.SubComponent;
import components.unitcapabilities.defaults.CastAbilities;
import components.unitcapabilities.defaults.Sprite;
import enums.AbilityName;
import imgui.ImGui;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import org.joml.Vector2f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;

public class Ability extends SubComponent {
    public AbilityName type;
    public float mp=0;
    public float range=-1;
    public Sprite sprite;
    public Boolean targetable=false;
    public String description="??UNKNOWN ABILITY??";
    public boolean Castable(float MP){
        return MP >= mp;
    }
    public Ability Copy(){
        System.out.println( "this ability has no copy function"+this.getClass());
        return null;
    }
    @Override
    public void updateData(HashMap<String, String> guiData, boolean running) {
        updateDesc();
    }
    public void updateDesc(){
        setDesc(type.getDesc());
    }
    public Ability(AbilityName type) {

        this.type=type;
        imguiGroup =1;
    }
    @Override
    public int getID(){
        return type.getId();
    }
    @Override
    public String RunningGui(int AbilitySize, List<GameObject> activeGameObjects, int ID){
        ImGui.tableNextColumn();
        ImGui.pushID(ID);
        Sprite Asprite=getSprite();
        Vector2f[] AtexCoords = Asprite.getTexCoords();


        if (ImGui.imageButton(Asprite.getTexId(), AbilitySize, AbilitySize, AtexCoords[2].x, AtexCoords[0].y, AtexCoords[0].x, AtexCoords[2].y)) {
            List<Integer> Ids=new ArrayList<Integer>();
            for (GameObject go:activeGameObjects) {
                CastAbilities cast= go.getComponent(CastAbilities.class);
                if (!(cast ==null)){
                    if (cast.isCastable(getID())){
                        Ids.add(go.getUid());
                        if (!KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)){
                            activeGameObjects.remove(go);
                            activeGameObjects.add(go); //this is imporant because it makes sure if you spam an ability it cycles through
                            break;
                        }
                    }
                }
            }
            if (Ids.size()>0){
                if(targetable) {
                    Window.targetCast(Ids, getID());
                }else{
                    Window.sendCast(Ids, getID());
                }
            }
        }ImGui.popID();
        if(ImGui.isItemHovered(0)){
//            ImGui.setNextWindowPos(ImGui.getMousePosX(),ImGui.getMousePosY());
////            if(ImGui.beginPopup("100")){
////                ImGui.inputText("55", new ImString("100"), ImGuiWindowFlags.NoMouseInputs);
////            }
////            ImGui.openPopup("100");
//
//            if(ImGui.beginChild("Desc",200,200,false, ImGuiWindowFlags.NoMouseInputs)) {
//
//
//                ImGui.inputText("55", new ImString("100"), ImGuiWindowFlags.NoMouseInputs);
//
//                }ImGui.endChild();

            return description;

        }else {

            return null;
        }

    }




    public void setDesc(String description){
        if(mp!=0) {

            this.description = description + "|4 costs" + (int) mp + "mp";
        }else{
            this.description = description;
        }
    }

    @Override
    public void start(){
        if (sprite.getTexture() != null) {
                sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
        }
}

    public void cast(final Vector2f pos, GameObject self){

    }
    public Sprite getSprite(){
        return sprite;
    }



}

    
    
    

