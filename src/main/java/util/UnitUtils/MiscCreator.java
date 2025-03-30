package util.UnitUtils;

import components.DamageZone;
import components.unitcapabilities.Mineral;
import components.unitcapabilities.defaults.Effects;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;

import java.util.ArrayList;
import java.util.HashMap;

import static enums.EffectName.timedLife;
import static jade.Prefabs.generateSpriteObject;
import static util.UnitUtils.BuildingCreator.genBuilding;
import static util.UnitUtils.CreatorTools.*;

public class MiscCreator {

    static public ArrayList<String> miscNames = new ArrayList<String>();
    static public HashMap<String,Float> miscStats = new HashMap<String, Float>();


    public static void init() {
        initStats("assets/Stats/MiscStats", miscStats,miscNames);

    }


    public static GameObject makeMisc(String name, Vector2f position, int allied) {
        name=name.toLowerCase();
        GameObject unit =switch(name){
            case "mineral0","mineral1","mineral2"->
                    BuildMineral(name,position,allied);
            case "puddle"->
                    BuildPuddle(name,position,allied);
            default->
                    genBase(name,position,allied);
        };
        return unit;

    }




    private static GameObject BuildMineral(String name, Vector2f position,int allied) {
        GameObject unit=genBuilding(name,position,allied);
        unit.getComponent(CircleCollider.class).setRadius(0.25f);
        unit.getComponent(Transform.class).scale.y*=0.4;
        unit.getComponent(Transform.class).scale.x*=0.5;
        unit.addComponent(new Mineral());


        return unit;
    }

    private static GameObject BuildPuddle(String name,Vector2f position,int allied){
        Vector2f size;
        if(cm(name,"size")){
            size=new Vector2f(m(name,"size"),m(name,"size"));
        }else{
            size=new Vector2f(m(name,"sizeX"),m(name,"sizeY"));
        }

        GameObject puddle=  generateSpriteObject(getSprite(name), size.x, size.y,name,position);
        puddle.allied=allied;
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        puddle.addComponent(rb);
        DamageZone dmg=new DamageZone(size.x,size.y);
        dmg.setDamage(10);
        dmg.damageFrequency=0.2;
        puddle.addComponent(dmg);
        Effects effects=new Effects();
        effects.addEffect(effects.getEffect(timedLife,10,1));
        puddle.addComponent(effects);



        return puddle;
    }
    private static float md(String currentItem,String stat,float defalt){
        return miscStats.getOrDefault(currentItem+"."+ stat.toLowerCase(),defalt);
    }
    private static float m(String currentItem,String stat) {
        return miscStats.get(currentItem+"."+ stat.toLowerCase());
    }
    private static boolean cm(String currentItem,String stat){
        return miscStats.containsKey(currentItem+"."+ stat.toLowerCase());
    }





}
