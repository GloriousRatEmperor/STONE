package util;

import SubComponents.Abilities.BuildBase;
import components.*;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static enums.AbilityName.*;
import static jade.Prefabs.generateSpriteObject;

public class Unit {
    static MapSpriteSheet items = AssetPool.getMapSheet("assets/images/spritesheets/joined.png");
    static public ArrayList<String> unitNames = new ArrayList<String>();
    static public ArrayList<String> buildNames = new ArrayList<String>();
    static public HashMap<String,Float> stats = new HashMap<String, Float>();
    static public HashMap<String,Float> Bstats = new HashMap<String, Float>();
    static public ArrayList<String> projectileNames = new ArrayList<String>();
    static public HashMap<String,Float> Pstats = new HashMap<String, Float>();

    public static void init() {

        File directoryPath = new File("assets/Stats");
        File[] fileList = directoryPath.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            try {

                File file = fileList[i];
                String fileName=file.getName().split("\\.")[0];
                unitNames.add(fileName);
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
                buildNames.add(fileName);
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
        directoryPath = new File("assets/ProjectileStats");
        fileList = directoryPath.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            try {

                File file = fileList[i];
                String fileName=file.getName().split("\\.")[0];
                projectileNames.add(fileName);
                List<String> inFile = Files.readAllLines(Paths.get(file.getPath()));
                for (String s:inFile){
                    String[] oneLine=s.split(":");
                    for (int h=0;h<oneLine.length; h++){
                        oneLine[h]=oneLine[h].replaceAll(" ", "").toLowerCase();

                    }
                    if(oneLine.length==2){
                        Pstats.put(fileName+oneLine[0], Float.parseFloat(oneLine[1]) );
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public static GameObject make(String name, Vector2f position,int allied){
        GameObject unit;
        switch(name.toLowerCase()){
            case "rock":
                unit=BuildWorker(name,position,allied,2);
                break;
            case "peasant":
                unit= BuildWorker(name,position,allied,1);
                break;
            case "whitler":
                unit= BuildWorker(name,position,allied,4);
                break;
            case "wisp":
                unit= BuildWorker(name,position,allied,3);
                break;
            default:
                unit=BuildGeneral(name,position,allied);
        }
        return unit;

    }
    public static GameObject makeBuilding(String name, Vector2f position,int allied){
        GameObject unit;
        switch(name.toLowerCase()){
            case "mineral0","mineral1","mineral2":
                unit= BuildMineral(name,position,allied);
                break;
            case "bloodbase","rockbase","magicbase","whitebase":
                unit= buildBase(name,position,allied);
                break;
            case "barracks":
                unit= buildBarracks(name,position,allied);
                break;
            default:
                unit=BuildGeneral(name,position,allied);
        }
        return unit;

    }
    public static GameObject makeProjectile(String name, Vector2f position,Transform target,int allied){
        GameObject unit;
        switch(name.toLowerCase()){
            case "arrowwwwwr":
                unit=BuildProjectile(name+"don't use me",position,target,allied+222222);
                break;
            default:
                unit=BuildProjectile(name,position,target,allied);
        }
        return unit;

    }
    private static GameObject BuildProjectile(String name, Vector2f position,Transform target,int allied){

        float sizeX,sizeY;
        if(Pstats.containsKey(name+"sizex")){
            sizeX=p(name+"sizex");
            sizeY=p(name+"sizey");
        }else{
            sizeX=sizeY= pd(name + "size", 1f);
        }
        GameObject proj = generateSpriteObject(items.getSprite(name),  sizeX, sizeY,name,position);
        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius((sizeX+sizeY)/4);
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(false);
        proj.addComponent(rb);
        Projectile p=new Projectile();
        p.damage=pd(name+"damage",10);
        p.speed=pd(name+"speed",5);
        p.attackSpeed=pd(name+"attackspeed",1);
        p.guided=p(name+"guided")==1f;
        proj.addComponent(p);
        p.moveCommand(target);
        return proj;
    }
    private static GameObject buildBase(String name, Vector2f position,int allied){
        CastAbilities c=new CastAbilities();
        switch (name) {
            case "bloodbase" -> {
                name = "bloodbase";
                c.addAbility(BuildPeasant);
            }
            case "rockbase" -> {
                name = "rockbase";
                c.addAbility(BuildRock);
            }
            case "magicbase" -> {
                name = "magicbase";
                c.addAbility(BuildWisp);
            }
            case "whitebase" -> {
                name = "whitebase";
                c.addAbility(BuildWhitler);c.addAbility(BuildPeasant);
                c.addAbility(BuildWisp);c.addAbility(BuildRock);

            }
        }
        c.addAbility(BuildBarracks);
        GameObject unit=genBuilding(name,position,allied);
        Mortal mort=new Mortal(b(name+"health"));
        unit.addComponent(mort);
        unit.addComponent(new Base());
        unit.addComponent(c);
        unit.addComponent(new UnitBuilder());
        return unit;
    }
    public static GameObject buildBarracks(String name, Vector2f position,int allied){
        GameObject unit=genBuilding(name,position,allied);
        unit.addComponent(new UnitBuilder());
        CastAbilities c =new CastAbilities();
        c.addAbility(c.getAbility(BuildTank));
        c.addAbility(c.getAbility(BuildBoarCavalary));
        Mortal mort=new Mortal(b(name+"health"));
        unit.addComponent(mort);
        unit.addComponent(c);



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




    private static GameObject BuildGeneral(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);
        if(stats.containsKey(name+"attack")){
            Hitter hit=new Hitter(u(name+"attack"),u(name+"attackspeed"));
            if(stats.containsKey(name+"chargebonus")){
                hit.chargeBonus=u(name+"chargebonus");
            }
            unit.addComponent(hit);
        }
        if(stats.containsKey(name+"health")) {
            Mortal mort = new Mortal(u(name + "health"));
            unit.addComponent(mort);
        }
        if(stats.containsKey(name+ "harvestamount")) {
            unit.addComponent(new Worker(u(name + "harvestamount")));
        }

        if(stats.containsKey(name+"speed")){
            MoveContollable move= new MoveContollable();
            move.speed=u(name+"speed");
            if(stats.containsKey(name+"acceleration")){
                move.acceleration=u(name+"acceleration");
                if(stats.containsKey(name+"turn")) {
                    move.turn = u(name + "turn");
                }
            }
            unit.addComponent(move);
        }


        float size=ud(name + "size", 0.9f);
        unit.getComponent(CircleCollider.class).setRadius(size/2);
        unit.transform.scale.set(new Vector2f(size,size));

        return unit;
    }
    public static GameObject BuildWorker(String name, Vector2f position,int allied,int race){
        GameObject worker= BuildGeneral(name,position,allied);
        CastAbilities c=new CastAbilities();
        BuildBase a=(BuildBase) c.getAbility(BuildBase);
        a.setRace(race);
        c.addAbility(a);
        if(race==4){
            for (int i=1;i<4;i++){
                SubComponents.Abilities.BuildBase b=(BuildBase) c.getAbility(BuildBase);
                b.setRace(i);
                c.addAbility(b);
            }
        }
        worker.addComponent(c);
        return worker;
    }


    private static GameObject genBase(String name, Vector2f position, int allied){

        GameObject newObject = generateSpriteObject(items.getSprite(name), 0.18f, 0.18f,name,position);
        newObject.allied=allied;



        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        newObject.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        newObject.addComponent(circleCollider);
        return newObject;
    }
    private static GameObject genBuilding(String name, Vector2f position, int allied){
        float sizeX,sizeY;
        if(Bstats.containsKey(name+"sizex")){
            sizeX=b(name+"sizex");
            sizeY=b(name+"sizey");
        }else{
            sizeX=sizeY= bd(name + "size", 1f);
        }
        GameObject newBuilding = generateSpriteObject(items.getSprite(name),  sizeX, sizeY,name,position);
        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius((sizeX+sizeY)/4);
        newBuilding.allied=allied;
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        newBuilding.addComponent(rb);


        newBuilding.addComponent(circleCollider);
        return newBuilding;
    }


    public static float getBuildTime(String name){
        if(!stats.containsKey(name+"buildtime")){
            System.out.println("BOZO this nay exist  "+name);
        }
        return u(name+"buildtime");
    }
    public static Vector3f getCost(String name){

        return new Vector3f( ud(name+"costblood",0f),ud(name+"costrock",0f),ud(name+"costmagic",0f));
    }
    public static Vector3f getBuildCost(String name){
        name=name.toLowerCase();
        return new Vector3f( bd(name+"costblood",0f),bd(name+"costrock",0f),bd(name+"costmagic",0f));
    }
    public static Sprite getSprite(String name){
        return items.getSprite(name);
    }


    private static float ud(String stat,float defalt){
        return stats.getOrDefault(stat,defalt);
    }
    private static float bd(String stat,float defalt){
        return Bstats.getOrDefault(stat,defalt);
    }
    private static float pd(String stat,float defalt){
        return Pstats.getOrDefault(stat,defalt);
    }
    private static float u(String stat) {
        return stats.get(stat);
    }

    private static float b(String stat){
        return Bstats.get(stat);
    }

    private static float p(String stat){
        return Pstats.get(stat);
    }


}
