package Abilitiess;

import Multiplayer.ServerData;
import components.CastAbilities;
import components.Sprite;
import enums.AbilityName;
import imgui.ImGui;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;

public class Ability {
    public String  name;
    public int id;
    public float mp=0;
    public transient Sprite sprite;
    private AbilityName type;
    public Boolean targetable=false;
    public String description="??UNKNOWN ABILITY??";
    public boolean Castable(float MP){
        return MP > mp;
    }
    public Ability Copy(){
        System.out.println( "this ability has no copy function"+this.getClass());
        return null;
    }
    public Ability(int id) {
        this.id = id;
    }
    public void setType(AbilityName type){
        this.type = type;
    }
    public AbilityName getType(){
        return this.type;
    }
    public void setName(String name){
        this.name = name;
    }
    public String Imgui(int AbilitySize, List<GameObject> activeGameObjects,int ID){
        ImGui.tableNextColumn();
        ImGui.pushID(ID);
        Sprite Asprite=getSprite();


        Asprite.setTexture(sprite.getTexture());
        Vector2f[] AtexCoords = Asprite.getTexCoords();


        if (ImGui.imageButton(Asprite.getTexId(), AbilitySize, AbilitySize, AtexCoords[2].x, AtexCoords[0].y, AtexCoords[0].x, AtexCoords[2].y)) {
            List<Integer> Ids=new ArrayList<Integer>();
            for (GameObject go:activeGameObjects) {
                CastAbilities cast= go.getComponent(CastAbilities.class);
                if (!(cast ==null)){
                    if (cast.isCastable(type)){
                        Ids.add(go.getUid());
                        if (KeyListener.isKeyPressed(GLFW_MOD_CONTROL)){
                            break;
                        }
                    }
                }
            }
            if (Ids.size()>0){
                if(targetable) {
                    Window.targetCast(Ids, name);
                }else{
                    Window.sendCast(Ids, name);
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
    public void start(){

    }
    public void setDesc(String description){
        if(mp!=0) {
            this.description = description + "|4 costs" + (int) mp + "mp";
        }else{
            this.description = description;
        }
    }



    public void cast(ServerData data, GameObject self){
    }
    public Sprite getSprite(){
        return sprite;
    }
}

    
    
    

