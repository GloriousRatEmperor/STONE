package util;

import Abilitiess.BuildBase;
import components.*;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.MoveContollable;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static enums.AbilityName.*;
import static jade.Prefabs.generateSpriteObject;

public class Unit {
    static MapSpriteSheet items = AssetPool.getMapSheet("assets/images/spritesheets/joined.png");
    static HashMap<String,Float> stats = new HashMap<String, Float>();
    static HashMap<String,Float> Bstats = new HashMap<String, Float>();
    public static void init() {
        File directoryPath = new File("assets/Stats");
        File[] fileList = directoryPath.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            try {

                File file = fileList[i];
                String fileName=file.getName().split("\\.")[0];
                List<String> inFile = Files.readAllLines(Paths.get(file.getPath()));
                for (String s:inFile){
                    String[] oneLine=s.split(":");
                    for (int h=0;h<oneLine.length; h++){
                        oneLine[h]=oneLine[h].replaceAll(" ", "").toLowerCase();

                    }
                    if(oneLine.length==2){
                        stats.put(fileName+oneLine[0], Float.parseFloat(oneLine[1]) );
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        directoryPath = new File("assets/BuildStats");
        fileList = directoryPath.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            try {

                File file = fileList[i];
                String fileName=file.getName().split("\\.")[0];
                List<String> inFile = Files.readAllLines(Paths.get(file.getPath()));
                for (String s:inFile){
                    String[] oneLine=s.split(":");
                    for (int h=0;h<oneLine.length; h++){
                        oneLine[h]=oneLine[h].replaceAll(" ", "").toLowerCase();

                    }
                    if(oneLine.length==2){
                        Bstats.put(fileName+oneLine[0], Float.parseFloat(oneLine[1]) );
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public static GameObject make(String name, Vector2f position,int allied){
        GameObject unit=null;
        switch(name){
            case "Rock":
                unit=BuildWorker(name,position,allied,2);
                break;
            case "Mineral0","Mineral1","Mineral2":
                unit= BuildMineral(name,position,allied);
                break;
            case "Base1","Base2","Base3","Base4":
                unit= buildBase(name,position,allied);
                break;
            case "Barracks":
                unit= buildBarracks(name,position,allied);
                break;
//            case "Tank":
//                unit= BuildTank(name,position,allied);
//                break;
            case "Peasant":
                unit= BuildWorker(name,position,allied,1);
                break;
            case "Whitler":
                unit= BuildWorker(name,position,allied,4);
                break;
            case "Whisp":
                unit= BuildWorker(name,position,allied,3);
                break;
            default:
                unit=BuildGeneral(name,position,allied);
        }
        return unit;

    }
    private static GameObject buildBase(String name, Vector2f position,int allied){
        CastAbilities c=new CastAbilities();
        switch (name) {
            case "Base1" -> {
                name = "magicblood";
                c.addAbility(BuildWhisp);
            }
            case "Base2" -> {
                name = "rockbase";
                c.addAbility(BuildRock);
            }
            case "Base3" -> {
                name = "bloodbase";
                c.addAbility(BuildPeasant);
            }
            case "Base4" -> {
                name = "whitebase";
                c.addAbility(BuildWhilter);
            }
        }

        GameObject unit=genBuilding(name,position,allied);
        Mortal mort=new Mortal(Bstats.get(name+"health"));
        unit.addComponent(mort);
        unit.transform.scale.mul(new Vector2f(3,3));
        unit.getComponent(CircleCollider.class).radius*=3;
        unit.addComponent(new Base());
        unit.addComponent(c);
        return unit;
    }
    public static GameObject buildBarracks(String name, Vector2f position,int allied){
        GameObject unit=genBuilding("rekrootment",position,allied);
        unit.transform.scale.mul(new Vector2f(2,2));
        unit.getComponent(CircleCollider.class).radius*=2;
        unit.addComponent(new UnitBuilder());
        CastAbilities c =new CastAbilities();
        c.addAbility(c.getAbility(BuildTank));
        Mortal mort=new Mortal(300);
        unit.addComponent(mort);
        unit.addComponent(c);


        return unit;
    }
    private static GameObject BuildMineral(String name, Vector2f position,int allied) {
        GameObject unit=genBuilding(name,position,allied);
        unit.getComponent(CircleCollider.class).setRadius(0.1f);
        unit.getComponent(Transform.class).scale.y*=2;
        unit.addComponent(new Mineral());


        return unit;
    }

    private static GameObject BuildRock(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        Hitter hit=new Hitter(8,0.3f);
        Mortal mort=new Mortal(30);
        unit.addComponent(mort);
        unit.addComponent(hit);
        unit.addComponent(new Worker());
        CastAbilities c=new CastAbilities();
        Abilitiess.BuildBase a=(BuildBase) c.getAbility(BuildBase);
        a.setRace(2);
        c.addAbility(a);





        unit.getComponent(CircleCollider.class).setRadius(0.09f);

        return unit;

    }
    private static GameObject BuildGeneral(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        if(stats.containsKey(name+"attack")){
            Hitter hit=new Hitter(stats.get(name+"attack"),stats.get(name+"attackspeed"));
            unit.addComponent(hit);
        }
        if(stats.containsKey(name+"health")) {
            Mortal mort = new Mortal(stats.get(name + "health"));
            unit.addComponent(mort);
        }
        if(stats.containsKey(name+ "harvestamount")) {
            unit.addComponent(new Worker(stats.get(name + "harvestamount")));
        }
        unit.getComponent(CircleCollider.class).setRadius(stats.getOrDefault(name + "size", 0.9f));
        return unit;
    }
    public static GameObject BuildWorker(String name, Vector2f position,int allied,int race){
        GameObject worker= BuildGeneral(name,position,allied);
        CastAbilities c=new CastAbilities();
        Abilitiess.BuildBase a=(BuildBase) c.getAbility(BuildBase);
        a.setRace(race);
        c.addAbility(a);
        return worker;
    }

    private static GameObject BuildPeasant(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        Hitter hit=new Hitter(5,0.2f);
        Mortal mort=new Mortal(27);
        unit.addComponent(mort);
        unit.addComponent(hit);
        unit.addComponent(new Worker());
        CastAbilities c=new CastAbilities();
        Abilitiess.BuildBase a=(BuildBase) c.getAbility(BuildBase);
        a.setRace(1);
        c.addAbility(a);


        unit.getComponent(CircleCollider.class).setRadius(0.09f);

        return unit;

    }
    private static GameObject BuildWhisp(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        Hitter hit=new Hitter(10,0.4f);
        Mortal mort=new Mortal(23);
        unit.addComponent(mort);
        unit.addComponent(hit);
        unit.addComponent(new Worker());
        CastAbilities c=new CastAbilities();
        Abilitiess.BuildBase a=(BuildBase) c.getAbility(BuildBase);
        a.setRace(3);
        c.addAbility(a);


        unit.getComponent(CircleCollider.class).setRadius(0.09f);

        return unit;

    }
    private static GameObject BuildWhitler(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        Hitter hit=new Hitter(3,0.1f);
        Mortal mort=new Mortal(23);
        unit.addComponent(mort);
        unit.addComponent(hit);
        unit.addComponent(new Worker());
        CastAbilities c=new CastAbilities();
        Abilitiess.BuildBase a=(BuildBase) c.getAbility(BuildBase);
        a.setRace(3);
        c.addAbility(a);


        unit.getComponent(CircleCollider.class).setRadius(0.09f);

        return unit;

    }
    private static GameObject BuildTank(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        Hitter hit=new Hitter(12,0.4f);
        Mortal mort=new Mortal(60);
        unit.addComponent(mort);
        unit.addComponent(hit);



        unit.getComponent(CircleCollider.class).setRadius(0.12f);
        unit.transform.scale.mul(new Vector2f(0.12f/0.09f,0.12f/0.09f));
        return unit;

    }

    private static GameObject genBase(String name, Vector2f position, int allied){

        GameObject fireball = generateSpriteObject(items.getSprite(name), 0.18f, 0.18f,name,position);
        fireball.allied=allied;
        fireball.addComponent(new MoveContollable());
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        fireball.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        fireball.addComponent(circleCollider);
        return fireball;
    }
    private static GameObject genBuilding(String name, Vector2f position, int allied){

        GameObject fireball = generateSpriteObject(items.getSprite(name), 0.18f, 0.18f,name,position);
        fireball.allied=allied;
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        fireball.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.18f);
        fireball.addComponent(circleCollider);
        return fireball;
    }


    public static float getBuildTime(String name){

        return stats.get(name+"buildtime");
    }

    public static Sprite getSprite(String name){
        return items.getSprite(name);
    }




}
