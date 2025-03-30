package util.UnitUtils;

import components.SubComponents.Effects.ImbuneAlternatorMark;
import components.gamestuff.StateMachine;
import components.unitcapabilities.Animation;
import components.unitcapabilities.CircleSensor;
import components.unitcapabilities.GuidedProjectile;
import components.unitcapabilities.LinearProjectile;
import components.unitcapabilities.defaults.Effects;
import components.unitcapabilities.defaults.Sprite;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;
import util.Img;

import java.util.ArrayList;
import java.util.HashMap;

import static jade.Prefabs.generateSpriteObject;
import static util.UnitUtils.CreatorTools.getSprite;
import static util.UnitUtils.CreatorTools.initStats;

public class ProjectileCreator {

    static public ArrayList<String> projectileNames = new ArrayList<String>();
    static public HashMap<String,Float> projectileStats = new HashMap<String, Float>();


    public static void init() {
        initStats("assets/Stats/ProjectileStats", projectileStats,projectileNames);

    }
    public static GameObject makeProjectile(String name,int ownerId, Vector2f position,Vector2f target,int allied){
        name=name.toLowerCase();
        GameObject unit =switch(name){
            case "alterbolt"->
                    BuildAlterbolt(name,position,target,allied,ownerId);
            default->
                    BuildLinearProjectile(name,position,target,allied,ownerId);
        };
        return unit;

    }

    public static GameObject makeProjectile(String name, int ownerId, Vector2f position, Transform target, int allied){
        name=name.toLowerCase();
        GameObject unit =switch(name){
            case "magicball"->
                    BuildMagicball(name,position,target,allied,ownerId);
            case "fireball"->
                    BuildFireball(name,position,target,allied,ownerId);

            default->
                    BuildGuidedProjectile(name,position,target,allied,ownerId);
        };
        return unit;

    }

    private static GameObject BuildFireball(String name, Vector2f position,Transform target,int allied,int ownerId){
        return BuildGuidedProjectile(name,position,target,allied,ownerId);

    }
    private static GameObject BuildMagicball(String name, Vector2f position,Transform target,int allied,int ownerId) {
        GameObject wraith= BuildGuidedProjectile(name,position,target,allied, ownerId);
        Animation animation = new Animation();
        Sprite magicball = Img.get("magicball");
        animation.addFrame(magicball, 0.3f, 1.5f, 90);
        animation.addFrame(magicball, 0.3f, 1/1.5f,90);
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
    private static GameObject BuildAlterbolt(String name, Vector2f position,Vector2f target,int allied,int ownerId){
        GameObject proj= BuildLinearProjectile(name,position,target,allied,ownerId);

        Effects e=proj.getComponent(Effects.class);
        e.addEffect(new ImbuneAlternatorMark(Float.MAX_VALUE,25));
        return proj;
    }
    private static GameObject BuildGuidedProjectile(String name, Vector2f position, Transform target, int allied, int ownerId){

        GameObject proj = BuildProjectileBase( name, position,allied,ownerId);

        GuidedProjectile p=new GuidedProjectile(pd(name,"damage",10),pd(name,"magicpercent",0));
        proj.addComponent(p);
        p.damage.owner=ownerId;
        p.speed=pd(name,"speed",5);
        p.lifespan=pd(name,"lifespan",20);
        p.attackSpeed=pd(name,"attackspeed",1);
        p.guided=true;
        p.prepMoveCommand(target);

        return proj;
    }
    private static GameObject BuildLinearProjectile(String name, Vector2f position, Vector2f pos, int allied, int ownerId){

        GameObject proj = BuildProjectileBase( name, position,allied,ownerId);
        LinearProjectile p=new LinearProjectile(pd(name,"damage",10),pd(name,"magicpercent",0));
        proj.addComponent(p);
        p.damage.owner=ownerId;
        p.speed=pd(name,"speed",5);
        p.attackSpeed=pd(name,"attackspeed",1);
        p.guided=false;
        p.lifespan=pd(name,"lifespan",20);
        p.prepMoveCommand(pos);


        float sizeX,sizeY;
        if(cp(name,"sizex")){
            sizeX=p(name,"sizex");
            sizeY=p(name,"sizey");
        }else{
            sizeX=sizeY= pd(name,"size", 1f);
        }
        CircleSensor circleCollider = new CircleSensor((sizeX+sizeY)/4);
        proj.addComponent(circleCollider);
        return proj;
    }
    private static GameObject BuildProjectileBase(String name, Vector2f position, int allied, int ownerId){

        float sizeX,sizeY;
        if(cp(name,"sizex")){
            sizeX=p(name,"sizex");
            sizeY=p(name,"sizey");
        }else{
            sizeX=sizeY= pd(name,"size", 1f);
        }
        GameObject proj = generateSpriteObject(getSprite(name),  sizeX, sizeY,name,position);
        proj.allied=allied;
        proj.addComponent(new Effects());
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(false);
        proj.addComponent(rb);
        return proj;
    }

    public static float calcRange(String projectileName){ //may misbehave with more complex projectile or if it doesn't reach
        float speed=p(projectileName.toLowerCase(),"speed");
        float acceleration=pd(projectileName.toLowerCase(),"acceleration",1);
        float lifespan=p(projectileName,"lifespan");
        if(acceleration!=1){
            lifespan-= (1/(acceleration*60f))/2; //Because: acceleration is in frames and lifespan is in seconds so *60,
            //divided by two because the first part is the time it takes to get to full speed so the average speed in that interval is half speed
        }
        return speed*lifespan;

    }
    private static float pd(String currentItem,String stat,float defalt){
        return projectileStats.getOrDefault(currentItem+"."+ stat.toLowerCase(),defalt);
    }
    private static float p(String currentItem,String stat){
        return projectileStats.get(currentItem+"."+ stat.toLowerCase());
    }
    private static boolean cp(String currentItem,String stat){
        return projectileStats.containsKey(currentItem+"."+ stat.toLowerCase());
    }

}
