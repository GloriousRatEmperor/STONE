package util.UnitUtils;

import components.unitcapabilities.Base;
import components.unitcapabilities.Brain;
import components.unitcapabilities.UnitBuilder;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.CastAbilities;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;

import java.util.ArrayList;
import java.util.HashMap;

import static enums.AbilityName.*;
import static jade.Prefabs.generateSpriteObject;
import static util.UnitUtils.CreatorTools.*;

public class BuildingCreator {


    static public ArrayList<String> buildNames = new ArrayList<String>();
    static public HashMap<String,Float> buildStats = new HashMap<String, Float>();

    public static void init() {
        initStats("assets/Stats/BuildStats", buildStats,buildNames);

    }

    public static GameObject makeBuilding(String name, Vector2f position, int allied){
        name=name.toLowerCase();
        GameObject unit =switch(name){

            case "bloodbase","rockbase","magicbase","whitebase"->
                    buildBase(name,position,allied);

            case "barracks"->
                    buildBarracks(name,position,allied);

            case "greenbarracks"->
                    buildGreenBarracks(name,position,allied);
            case "morticum"->
                    buildMorticum(name,position,allied);

            default->
                    genBuilding(name,position,allied);
        };
        return unit;

    }

    private static GameObject buildBase(String name, Vector2f position,int allied){
        CastAbilities c=new CastAbilities();
        name=name.toLowerCase();
        switch (name) {
            case "bloodbase" -> {
                c.addAbility(buildPeasant);
            }
            case "rockbase" -> {
                c.addAbility(buildRock);
            }
            case "magicbase" -> {

                c.addAbility(buildWisp);
            }
            case "whitebase" -> {
                c.addAbility(buildWhitler);c.addAbility(buildPeasant);
                c.addAbility(buildWisp);c.addAbility(buildRock);

            }
        }
        GameObject unit=genBuilding(name,position,allied);
        unit.addComponent(new Base());
        unit.addComponent(c);
        unit.addComponent(new UnitBuilder());
        return unit;
    }
    public static GameObject buildBarracks(String name, Vector2f position,int allied){
        GameObject unit=genBuilding(name,position,allied);
        unit.addComponent(new UnitBuilder());
        CastAbilities c =new CastAbilities();
        c.addAbility(c.makeAbility(buildPebble));
        c.addAbility(c.makeAbility(buildTank));
        c.addAbility(c.makeAbility(buildPriest));
        c.addAbility(c.makeAbility(buildStoneborn));
        c.addAbility(c.makeAbility(buildVolcano));

        unit.addComponent(c);



        return unit;
    }
    public static GameObject buildGreenBarracks(String name, Vector2f position,int allied){
        GameObject unit=genBuilding(name,position,allied);
        unit.addComponent(new UnitBuilder());
        CastAbilities c =new CastAbilities();
        c.addAbility(c.makeAbility(buildSnek));
        c.addAbility(c.makeAbility(buildBoarCavalary));
        c.addAbility(c.makeAbility(buildChicken));
        c.addAbility(c.makeAbility(buildSpearman));
        c.addAbility(c.makeAbility(buildSpider));
        unit.addComponent(c);



        return unit;
    }
    public static GameObject buildMorticum(String name, Vector2f position,int allied){
        GameObject unit=genBuilding(name,position,allied);
        unit.addComponent(new UnitBuilder());
        CastAbilities c =new CastAbilities();
        c.addAbility(c.makeAbility(buildwraith));
        c.addAbility(c.makeAbility(buildHeadless));
        c.addAbility(c.makeAbility(buildAlterator));
        c.addAbility(buildBuffBird);
        c.addAbility(c.makeAbility(getBuildHeadlessHorseman));
        unit.addComponent(c);



        return unit;
    }

    static GameObject genBuilding(String name, Vector2f position, int allied){
        float sizeX,sizeY;
        if(cb(name,"sizex")){
            sizeX=b(name,"sizex");
            sizeY=b(name,"sizey");
        }else{
            sizeX=sizeY= bd( name,"size", 1f);
        }
        GameObject newBuilding = generateSpriteObject(getSprite(name),  sizeX, sizeY,name,position);
        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius((sizeX+sizeY)/4);
        newBuilding.allied=allied;
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        newBuilding.addComponent(rb);
        newBuilding.addComponent(new Brain());

        newBuilding.addComponent(circleCollider);
        if(cb(name,"health")) {
            Mortal mort = new Mortal(b( name,"health"), bd( name,"armor", 0));
            newBuilding.addComponent(mort);
        }

        return newBuilding;
    }


    public static Vector3f getBuildCost(String name){
        name=name.toLowerCase();
        Vector3f ret=new Vector3f( bd(name,"costblood",0f),bd(name,"costrock",0f),bd(name,"costmagic",0f));


        return ret;

    }
    private static float bd(String currentItem,String stat,float defalt){
        return buildStats.getOrDefault(currentItem+"."+ stat.toLowerCase(),defalt);
    }
    private static float b(String currentItem,String stat){
        return buildStats.get(currentItem+"."+ stat.toLowerCase());
    }
    private static boolean cb(String currentItem,String stat){
        return buildStats.containsKey(currentItem+"."+ stat.toLowerCase());
    }

}
