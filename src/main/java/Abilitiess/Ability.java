package Abilitiess;

import Multiplayer.ServerData;
import components.CastAbilities;
import components.Sprite;
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
    public Sprite sprite=new Sprite();
    public boolean Castable(float MP){
        return MP > mp;
    }
    public Ability Copy(){
        System.out.println( "this ability has no copy function"+this.getClass());
        return null;
    }
    public Ability(String a, int id) {
        this.name = a;
        this.id = id;
    }
    public void Imgui(int AbilitySize, List<GameObject> activeGameObjects,int ID){
        ImGui.tableNextColumn();
        ImGui.pushID(ID);
        Sprite Asprite=getSprite();
        Asprite.setTexture(sprite.getTexture());
        Vector2f[] AtexCoords = Asprite.getTexCoords();


        // TODO Texture Id is mysteriously 6??? it should be five? one ugly fiox later and it works I guess??
        if (ImGui.imageButton(5, AbilitySize, AbilitySize, AtexCoords[2].x, AtexCoords[0].y, AtexCoords[0].x, AtexCoords[2].y)) {
            List<Integer> Ids=new ArrayList<Integer>();
            for (GameObject go:activeGameObjects) {
                CastAbilities cast= go.getComponent(CastAbilities.class);
                if (!(cast ==null)){
                    if (cast.isCastable(name)){
                        Ids.add(go.getUid());
                        if (!KeyListener.isKeyPressed(GLFW_MOD_CONTROL)){
                            break;
                        }
                    }
                }
            }
            if (Ids.size()>0){
                Window.sendCast(Ids,name);
            }
        }ImGui.popID();
    }
    public void start(){

    }



    public void cast(ServerData data, GameObject self){
    }
    public Sprite getSprite(){
        return sprite;
    }
}

    
    
    

