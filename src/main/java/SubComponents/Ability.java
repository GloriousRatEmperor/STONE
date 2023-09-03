package SubComponents;

import components.Mortal;
import components.Sprite;
import components.Spritesheet;
import enums.AbilityName;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2f;
import physics2d.components.MoveContollable;
import physics2d.components.Rigidbody2D;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Objects;

public class Ability {
    public String  name;
    public int id;
    public float mp;
    protected Sprite sprite=new Sprite();
    public Ability(AbilityName a, int id) {
        this.name = a.name();
        this.id = id;
    }
    public void Trigger(Vector2f position, ArrayList<Integer> id){



    }
    public void cast(Vector2f position, GameObject self){
    }
    public Sprite getSprite(){
        return sprite;
    }
}

    
    
    

