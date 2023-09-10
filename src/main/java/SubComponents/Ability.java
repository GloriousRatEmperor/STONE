package SubComponents;

import components.CastAbilities;
import components.Mortal;
import components.Sprite;
import components.Spritesheet;
import enums.AbilityName;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector2f;
import physics2d.components.MoveContollable;
import physics2d.components.Rigidbody2D;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ability {
    public String  name;
    public int id;
    public float mp;
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
            for (GameObject go:activeGameObjects) {
                CastAbilities cast= go.getComponent(CastAbilities.class);
                if (!(cast ==null)){
                    cast.castAbility(name, MouseListener.getScreen());
                }
            }
        }ImGui.popID();
    }
    public void start(){

    }
    public void Trigger(Vector2f position, ArrayList<Integer> id){



    }
    public void cast(Vector2f position, GameObject self){
    }
    public Sprite getSprite(){
        return sprite;
    }
}

    
    
    

