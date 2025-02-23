package util;

import components.SubComponents.Abilities.BuildBase;
import components.SubComponents.Effects.ExplodingProjectiles;
import components.gamestuff.MapSpriteSheet;
import components.gamestuff.StateMachine;
import components.unitcapabilities.*;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.CastAbilities;
import components.unitcapabilities.defaults.Effects;
import components.unitcapabilities.damage.Hitter;
import components.unitcapabilities.defaults.MoveContollable;
import components.unitcapabilities.defaults.Sprite;
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
import java.util.*;

import static enums.AbilityName.*;
import static enums.EffectName.explodingProjectiles;
import static jade.Prefabs.generateSpriteObject;
import static jade.Window.getScene;

public class Unit {
    static MapSpriteSheet items = AssetPool.getMapSheet("assets/images/spritesheets/joined.png");
    static public ArrayList<String> unitNames = new ArrayList<String>();
    static public ArrayList<String> buildNames = new ArrayList<String>();
    static public HashMap<String,Float> stats = new HashMap<String, Float>();
    static public HashMap<String,Float> unlockCosts = new HashMap<String, Float>();
    static public HashMap<String,Float> Bstats = new HashMap<String, Float>();
    static public ArrayList<String> projectileNames = new ArrayList<String>();
    static public HashMap<String,Float> Pstats = new HashMap<String, Float>();

    static String lastItem;

    public static void init() {

        File directoryPath = new File("assets/Stats");
        File[] fileList = directoryPath.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            try {

                File file = fileList[i];
                String fileName=file.getName().toLowerCase().split("\\.")[0];
                unitNames.add(fileName);
                List<String> inFile = Files.readAllLines(Paths.get(file.getPath()));
                for (String s:inFile){
                    String[] oneLine=s.split(":");
                    for (int h=0;h<oneLine.length; h++){
                        oneLine[h]=oneLine[h].replaceAll(" ", "").toLowerCase();

                    }
                    if(oneLine.length==2){
                        if(oneLine[0].startsWith("unlock")){
                            unlockCosts.put(fileName+"."+oneLine[0].substring(6), Float.parseFloat(oneLine[1]) );
                            continue;
                        }
                        stats.put(fileName+"."+oneLine[0], Float.parseFloat(oneLine[1]) );
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
                String fileName=file.getName().toLowerCase().split("\\.")[0];
                buildNames.add(fileName);
                List<String> inFile = Files.readAllLines(Paths.get(file.getPath()));
                for (String s:inFile){
                    String[] oneLine=s.split(":");
                    for (int h=0;h<oneLine.length; h++){
                        oneLine[h]=oneLine[h].replaceAll(" ", "").toLowerCase();

                    }
                    if(oneLine.length==2){
                        if(oneLine[0].startsWith("unlock")){
                            unlockCosts.put(fileName+"."+oneLine[0].substring(6), Float.parseFloat(oneLine[1]));
                            continue;
                        }
                        Bstats.put(fileName+"."+oneLine[0], Float.parseFloat(oneLine[1]) );
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
                String fileName=file.getName().toLowerCase().split("\\.")[0];
                projectileNames.add(fileName);
                List<String> inFile = Files.readAllLines(Paths.get(file.getPath()));
                for (String s:inFile){
                    String[] oneLine=s.split(":");
                    for (int h=0;h<oneLine.length; h++){
                        oneLine[h]=oneLine[h].replaceAll(" ", "").toLowerCase();

                    }
                    if(oneLine.length==2){
                        if(oneLine[0].startsWith("unlock")){
                            unlockCosts.put(fileName+"."+oneLine[0].substring(6), Float.parseFloat(oneLine[1]));
                            continue;
                        }
                        
                        Pstats.put(fileName+"."+oneLine[0], Float.parseFloat(oneLine[1]) );
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public static GameObject make(String name, Vector2f position,int allied){
        name=name.toLowerCase();
        GameObject unit =switch(name){
            case "rock"->
                BuildWorker(name,position,allied,2);
            case "spearman"->
                    BuildSpearman(name,position,allied);
            case "peasant"->
                BuildWorker(name,position,allied,1);

            case "whitler"->
                BuildWorker(name,position,allied,4);

            case "wisp"->
                BuildWorker(name,position,allied,3);

            case "snek"->
                BuildSnake(name,position,allied);
            case "wraith"->
                    BuildWraith(name,position,allied);
            case "priest"->
                BuildPriest(name,position,allied);
            default->
                BuildGeneral(name,position,allied);
        };
        return unit;

    }
    public static GameObject makeBuilding(String name, Vector2f position,int allied){
        name=name.toLowerCase();
        GameObject unit =switch(name){
            case "mineral0","mineral1","mineral2"->
                 BuildMineral(name,position,allied);

            case "bloodbase","rockbase","magicbase","whitebase"->
                 buildBase(name,position,allied);

            case "barracks"->
                 buildBarracks(name,position,allied);

            case "greenbarracks"->
                buildGreenBarracks(name,position,allied);
            case "morticum"->
                    buildMorticum(name,position,allied);

            default->
                BuildGeneral(name,position,allied);
        };
        return unit;

    }
    public static GameObject makeProjectile(String name,int ownerId, Vector2f position,Transform target,int allied){
        name=name.toLowerCase();
        GameObject unit =switch(name){
            case "magicball"->
                BuildMagicball(name,position,target,allied,ownerId);
            case "fireball"->
                BuildFireball(name,position,target,allied,ownerId);

            default->
                BuildProjectile(name,position,target,allied,ownerId);
        };
        return unit;

    }
    private static GameObject BuildFireball(String name, Vector2f position,Transform target,int allied,int ownerId){
        return BuildProjectile(name,position,target,allied,ownerId);

    }
    private static GameObject BuildMagicball(String name, Vector2f position,Transform target,int allied,int ownerId) {
        GameObject wraith=BuildProjectile(name,position,target,allied, ownerId);
        AnimationState animation = new AnimationState();
        Sprite magicball = Img.get("magicball");
        animation.addFrame(magicball, 0.3f, 1.5f, 0);
        animation.addFrame(magicball, 0.3f, 1/1.5f,0);
        animation.setLoop(true);
        animation.setDeathOnCompletion(false);
        animation.title = "wraithball";

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animation);
        stateMachine.addState(animation.title, animation.title, animation.title);
        stateMachine.setDefaultState(animation.title);
        stateMachine.trigger(animation.title);
        wraith.addComponent(stateMachine);
        return wraith;
    }
    private static GameObject BuildProjectile(String name, Vector2f position,Transform target,int allied,int ownerId){

        float sizeX,sizeY;
        if(cp(name,"sizex")){
            sizeX=p(name,"sizex");
            sizeY=p(name,"sizey");
        }else{
            sizeX=sizeY= pd(name,"size", 1f);
        }
        GameObject proj = generateSpriteObject(items.getSprite(name),  sizeX, sizeY,name,position);
        proj.allied=allied;
        proj.addComponent(new Effects());
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(false);
        proj.addComponent(rb);
        Projectile p=new Projectile(pd(name,"damage",10));
        p.damage.owner=ownerId;
        p.speed=pd(name,"speed",5);
        p.attackSpeed=pd(name,"attackspeed",1);
        p.guided=p(name,"guided")==1f;
        if(!p.guided){
            CircleCollider circleCollider = new CircleCollider();
            circleCollider.setRadius((sizeX+sizeY)/4);
            proj.addComponent(circleCollider);
        }
        proj.addComponent(p);
        p.moveCommand(target);
        return proj;
    }
    private static GameObject buildBase(String name, Vector2f position,int allied){
        CastAbilities c=new CastAbilities();
        name=name.toLowerCase();
        switch (name) {
            case "bloodbase" -> {
                c.addAbility(buildPeasant);
                c.addAbility(buildGreenBarracks);
            }
            case "rockbase" -> {
                c.addAbility(buildRock);
                c.addAbility(buildBarracks);
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
        unit.addComponent(c);



        return unit;
    }
    public static GameObject buildMorticum(String name, Vector2f position,int allied){
        GameObject unit=genBuilding(name,position,allied);
        unit.addComponent(new UnitBuilder());
        CastAbilities c =new CastAbilities();
        c.addAbility(c.makeAbility(buildwraith));
        c.addAbility(c.makeAbility(buildHeadless));
        c.addAbility(c.makeAbility(getBuildHeadlessHorseman));
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


    private static GameObject BuildSnake(String name, Vector2f position,int allied){
        GameObject snek= BuildGeneral(name,position,allied);
        Shooter fballer=new Shooter(3.5f,6,"fireball");

        snek.addComponent(fballer);
        Effects effects=snek.getComponent(Effects.class);
        ExplodingProjectiles e=(ExplodingProjectiles) effects.getEffect(explodingProjectiles);
        e.durationNow= Float.MAX_VALUE;
        e.durationTotal= Float.MAX_VALUE;
        e.radius=1.5f;
        e.damage=40;
        fballer.addEffect(e);
        return snek;

    }
    private static GameObject BuildWraith(String name,Vector2f position,int allied){
        GameObject wraith= BuildGeneral(name,position,allied);
        Shooter fballer=new Shooter(3,1.25f,"magicball");
        wraith.removeComponent(Brain.class);
        wraith.addComponent(new RangedBrain());

        wraith.addComponent(fballer);
        return wraith;
    }
    private static GameObject BuildPriest(String name,Vector2f position,int allied){
        GameObject priest= BuildGeneral(name,position,allied);
        CastAbilities cast=new CastAbilities();
        cast.mpRegen=0.1f;
        cast.addAbility(cast.makeAbility(heal));
        priest.addComponent(cast);
        return priest;
    }
    private static GameObject BuildSpearman(String name,Vector2f position,int allied){
        GameObject spearman= BuildGeneral(name,position,allied);
        spearman.getComponent(Mortal.class).chargeDefense=u(name,"chargedefense");
        return spearman;
    }
    private static GameObject BuildGeneral(String name, Vector2f position,int allied){
        GameObject unit=genBase(name,position,allied);

        if(cu(name,"attack")){
            Hitter hit=new Hitter(u(name,"attack"),u(name,"attackspeed"));
            if(cu(name,"chargebonus")){
                hit.chargeBonus=u(name,"chargebonus");
            }
            unit.addComponent(hit);
        }
        if(cu(name,"health")) {
            Mortal mort = new Mortal(u( name,"health"),ud(name,"armor",0));
            unit.addComponent(mort);
        }
        if(cu( name,"harvestamount")) {
            unit.addComponent(new Worker(u( name,"harvestamount")));
        }

        if(cu(name,"speed")){
            MoveContollable move= new MoveContollable();
            move.speed=u(name,"speed");
            if(cu(name,"acceleration")){
                move.acceleration=u(name,"acceleration");
                if(cu(name,"turn")) {
                    move.turn = u( name,"turn");
                }
            }
            unit.addComponent(move);
        }


        float size=ud( name,"size", 0.9f);
        unit.getComponent(CircleCollider.class).setRadius(size/2);
        unit.transform.scale.set(new Vector2f(size,size));
        unit.addComponent(new Effects());
        return unit;
    }
    public static GameObject BuildWorker(String name, Vector2f position,int allied,int race){
        GameObject worker= BuildGeneral(name,position,allied);
        CastAbilities c=new CastAbilities();
        BuildBase a=(BuildBase) c.makeAbility(buildBase);
        a.setRace(race);
        c.addAbility(a);

        if(race==4){
            for (int i=1;i<4;i++){
                BuildBase b=(BuildBase) c.makeAbility(buildBase);
                b.setRace(i);
                c.addAbility(b);
            }
        }else{
            switch (race){
                case (1)->
                        c.addAbility(buildGreenBarracks);
                case (2)->
                        c.addAbility(buildBarracks);
                case (3)->
                        c.addAbility(buildMorticum);
            }
        }

        worker.addComponent(c);
        return worker;
    }


    private static GameObject genBase(String name, Vector2f position, int allied){

        GameObject newObject = generateSpriteObject(items.getSprite(name), 0.18f, 0.18f,name,position);
        newObject.allied=allied;
        newObject.addComponent(new Brain());
        newObject.addComponent(new aggroDetector());


        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        newObject.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        newObject.addComponent(circleCollider);
        newObject.addComponent(new Effects());
        return newObject;
    }
    private static GameObject genBuilding(String name, Vector2f position, int allied){
        float sizeX,sizeY;
        if(cb(name,"sizex")){
            sizeX=b(name,"sizex");
            sizeY=b(name,"sizey");
        }else{
            sizeX=sizeY= bd( name,"size", 1f);
        }
        GameObject newBuilding = generateSpriteObject(items.getSprite(name),  sizeX, sizeY,name,position);
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


    public static float getBuildTime(String name){
        name=name.toLowerCase();

        if(!stats.containsKey(name+"."+"buildtime")){
            System.out.println("BOZO this nay exist  "+name);
        }
        float ret=u(name,"buildtime");
        return ret;
    }
    public static Vector3f getCost(String name){

        name=name.toLowerCase();
        Vector3f ret=new Vector3f( ud(name,"costblood",0f),ud(name,"costrock",0f),ud(name,"costmagic",0f));

        return ret;
    }
    public static Vector3f getBuildCost(String name){
        name=name.toLowerCase();
        Vector3f ret=new Vector3f( bd(name,"costblood",0f),bd(name,"costrock",0f),bd(name,"costmagic",0f));


        return ret;

    }
    public static Vector3f getUnlockCost(String name){
        name=name.toLowerCase();
        
        return new Vector3f( unlockCosts.getOrDefault(name+"."+"costblood",0f)
                ,unlockCosts.getOrDefault(name+"."+"costrock",0f)
                ,unlockCosts.getOrDefault(name+"."+"costmagic",0f));
    }
    public static String getUStats(String unitname){
        StringBuilder result= new StringBuilder();
        Set<String> keys= new HashSet<>(stats.keySet().stream().filter(stat -> stat.startsWith(unitname+".")).toList());
        keys.remove(unitname+"."+"costblood");
        keys.remove(unitname+"."+"costrock");
        keys.remove(unitname+"."+"costmagic");


        if(keys.contains(unitname+"."+"health")){
            keys.remove(unitname+"."+"health");
            result.append("|1 Health").append(u( unitname,"health"));
        }
        if(keys.contains(unitname+"."+"armor")){
            keys.remove(unitname+"."+"armor");
            result.append("|5 Armor").append(u( unitname,"armor"));
        }
        if(keys.contains(unitname+"."+"attack")){
            keys.remove(unitname+"."+"attack");
            result.append("|7 Attack").append(u( unitname,"attack"));
        }
        if(keys.contains(unitname+"."+"attackspeed")){
            keys.remove(unitname+"."+"attackspeed");
            result.append("|6 Attackspeed").append(u( unitname,"attackspeed"));
        }
        if(keys.contains(unitname+"."+"speed")){
            keys.remove(unitname+"."+"speed");
            result.append("|2 Speed").append(u( unitname,"speed"));
        }
        if(!keys.isEmpty()) {
            result.append("|0");
            for (String key : keys) {
                result.append(" ").append(key.substring(unitname.length()+1)).append(" ").append(stats.get(key));
            }
        }
        return result.toString();

    }
    public static Sprite getSprite(String name){
        return items.getSprite(name);
    }


    private static float ud(String currentItem,String stat,float defalt){
        return stats.getOrDefault(currentItem+"."+ stat,defalt);
    }
    private static float bd(String currentItem,String stat,float defalt){
        return Bstats.getOrDefault(currentItem+"."+ stat,defalt);
    }
    private static float pd(String currentItem,String stat,float defalt){
        return Pstats.getOrDefault(currentItem+"."+ stat,defalt);
    }
    private static float u(String currentItem,String stat) {
        return stats.get(currentItem+"."+ stat);
    }

    private static float b(String currentItem,String stat){
        return Bstats.get(currentItem+"."+ stat);
    }

    private static float p(String currentItem,String stat){
        return Pstats.get(currentItem+"."+ stat);
    }
    private static boolean cu(String currentItem,String stat){
        return stats.containsKey(currentItem+"."+ stat);
    }
    private static boolean cb(String currentItem,String stat){
        return Bstats.containsKey(currentItem+"."+ stat);
    }
    private static boolean cp(String currentItem,String stat){
        return Pstats.containsKey(currentItem+"."+ stat);
    }
    public static void generateAnimation(Vector2f position, float sizeX, float sizeY, AnimationState animation) {
        GameObject anime = generateSpriteObject(animation.getFirstSprite(), sizeX, sizeY, "disposableTemp", position);
        StateMachine stateMachine = new StateMachine();
        animation.title = "Animation";
        animation.setLoop(false);
        animation.setDeathOnCompletion(true);
        anime.addComponent(stateMachine);
        stateMachine.addState(animation);
        stateMachine.addState(animation.title, animation.title, animation.title);
        stateMachine.setDefaultState(animation.title);
        stateMachine.trigger(animation.title);
        anime.setNoSerialize();
        anime.addComponent(new NonPickable());
        anime.start();
        getScene().addDrawObjecttoScene(anime);
    }

}
