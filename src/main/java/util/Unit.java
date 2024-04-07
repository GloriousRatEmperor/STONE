package util;

import components.*;
import jade.GameObject;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;

import java.util.ArrayList;
import java.util.HashMap;

import static jade.Prefabs.generateSpriteObject;

public class Unit {
    static MapSpriteSheet items = AssetPool.getMapSheet("assets/images/spritesheets/joined.png");
    static HashMap<String, Float> buildtime= new HashMap<String, Float>();
    public static void init(){
        buildtime.put("Rock", 3f);
    }
    public static GameObject make(String name, Vector2f position,int allied){
        GameObject unit=null;
        switch(name){
            case "Rock":
                unit= BuildRock(name,position,allied);
        }
        if(unit==null){
            System.out.println("WTF YOU MAKING BOZO"+name+"U RETARD?!");
        }
        return unit;

    }
    private static GameObject BuildRock(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        Hitter hit=new Hitter();
        Mortal mort=new Mortal();
        unit.addComponent(mort);
        unit.addComponent(hit);



        hit.dmg=15;
        hit.attackSpeed=0.7f;
        mort.health=55;

        unit.getComponent(CircleCollider.class).setRadius(0.09f);

        return unit;

    }

    private static GameObject genBase(String name, Vector2f position, int allied){

        GameObject fireball = generateSpriteObject(items.getSprite(name), 0.18f, 0.18f);
        fireball.allied=allied;
        fireball.transform.position = position;

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        fireball.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        fireball.addComponent(circleCollider);
        return fireball;
    }


    public static float getBuildTime(String name){
        return buildtime.get(name);
    }






}
