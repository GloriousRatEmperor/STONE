package util.UnitUtils;

import components.SubComponents.Abilities.BuildBase;
import components.SubComponents.Abilities.Errupt;
import components.SubComponents.Effects.ExplodingProjectiles;
import components.SubComponents.Effects.FearProjectiles;
import components.unitcapabilities.Brain;
import components.unitcapabilities.RangedBrain;
import components.unitcapabilities.Shooter;
import components.unitcapabilities.Worker;
import components.unitcapabilities.damage.Hitter;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.CastAbilities;
import components.unitcapabilities.defaults.Effects;
import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2d.components.CircleCollider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static enums.AbilityName.*;
import static enums.AbilityName.errupt;
import static enums.EffectName.explodingProjectiles;
import static util.UnitUtils.CreatorTools.*;

public class UnitCreator {

    static public ArrayList<String> unitNames = new ArrayList<String>();
    static public HashMap<String,Float> unitStats = new HashMap<String, Float>();

    public static void init() {
        initStats("assets/Stats/Unit_stats", unitStats,unitNames);

    }




    public static GameObject makeUnit(String name, Vector2f position, int allied){
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
            case "volcano"->
                    BuildVolcano(name,position,allied);
            case "spider"->
                    BuildSpider(name,position,allied);
            case "alterator"->
                    BuildAlterator(name,position,allied);
            default->
                    BuildGeneralUnit(name,position,allied);
        };
        return unit;

    }

    private static GameObject BuildSnake(String name, Vector2f position,int allied){
        GameObject snek= BuildGeneralUnit(name,position,allied);
        Shooter fballer=new Shooter(u(name,"range"),u(name,"rangedattackspeed"),"fireball");

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
    private static GameObject BuildAlterator(String name,Vector2f position,int allied) {
        GameObject alter= BuildGeneralUnit(name,position,allied);
        CastAbilities cast=alter.getComponent(CastAbilities.class);
        cast.addAbility(cast.makeAbility(shootAlterBolt));
        cast.addAbility(cast.makeAbility(teleportMarked));
        cast.maxmp=100;
        cast.mp=0;
        cast.mpRegen=0.1f;
        return alter;
    }
    private static GameObject BuildWraith(String name,Vector2f position,int allied){
        GameObject wraith= BuildGeneralUnit(name,position,allied);
        Shooter fballer=new Shooter(u(name,"range"),u(name,"rangedattackspeed"),"magicball");

        wraith.removeComponent(Brain.class);
        wraith.addComponent(new RangedBrain());

        wraith.addComponent(fballer);
        return wraith;
    }
    private static GameObject BuildSpider(String name,Vector2f position,int allied){
        GameObject spider= BuildGeneralUnit(name,position,allied);
        Shooter fballer=new Shooter(u(name,"range"),u(name,"rangedattackspeed"),"skull");
        fballer.addEffect(new FearProjectiles(Float.MAX_VALUE,7.5f));
        spider.removeComponent(Brain.class);
        spider.addComponent(new RangedBrain());
        spider.addComponent(fballer);
        return spider;
    }
    private static GameObject BuildPriest(String name,Vector2f position,int allied){
        GameObject priest= BuildGeneralUnit(name,position,allied);
        CastAbilities cast=priest.getComponent(CastAbilities.class);
        cast.maxmp=100;
        cast.mp=50;
        cast.mpRegen=0.1f;
        cast.addAbility(cast.makeAbility(heal));
        return priest;
    }
    private static GameObject BuildVolcano(String name,Vector2f position,int allied){
        GameObject volcano= BuildGeneralUnit(name,position,allied);
        CastAbilities cast=volcano.getComponent(CastAbilities.class);
        Errupt erupt=(Errupt) cast.makeAbility(errupt);
        erupt.damage=100;
        erupt.radius=1;

        cast.addAbility(erupt);
        return volcano;
    }

    private static GameObject BuildSpearman(String name,Vector2f position,int allied){
        GameObject spearman= BuildGeneralUnit(name,position,allied);
        spearman.getComponent(Mortal.class).chargeDefense=u(name,"chargedefense");
        return spearman;
    }

    public static GameObject BuildWorker(String name, Vector2f position,int allied,int race){
        GameObject worker= BuildGeneralUnit(name,position,allied);
        CastAbilities c=worker.getComponent(CastAbilities.class);
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

        return worker;
    }
    public static Vector3f getUnitCost(String name){

        name=name.toLowerCase();
        Vector3f ret=new Vector3f( ud(name,"costblood",0f),ud(name,"costrock",0f),ud(name,"costmagic",0f));

        return ret;
    }

    public static String getUStats(String unitname){
        //colors in imguiDescription
        StringBuilder result= new StringBuilder();
        Set<String> keys= new HashSet<>(unitStats.keySet().stream().filter(stat -> stat.startsWith(unitname+".")).toList());
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
        if(keys.contains(unitname+"."+"magicarmor")){
            keys.remove(unitname+"."+"magicarmor");
            result.append("|8 magicArmor").append(u( unitname,"magicarmor"));
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
                result.append(" ").append(key.substring(unitname.length()+1)).append(" ").append(unitStats.get(key));
            }
        }
        return result.toString();

    }
    public static GameObject BuildGeneralUnit(String name, Vector2f position, int allied){
        GameObject unit=genBase(name,position,allied);
        CastAbilities cast=new CastAbilities();
        cast.addAbility(cast.makeAbility(guardMode));
        unit.addComponent(cast);
        if(cu(name,"attack")){
            Hitter hit=new Hitter(u(name,"attack"),u(name,"attackspeed"));
            if(cu(name,"chargebonus")){
                hit.chargeBonus=u(name,"chargebonus");
            }
            unit.addComponent(hit);
        }
        if(cu(name,"health")) {
            Mortal mort = new Mortal(u( name,"health"),ud(name,"armor",0));
            mort.magicArmor=ud(name,"magicarmor",0);
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
    public static float getBuildTime(String name){
        name=name.toLowerCase();

        if(!unitStats.containsKey(name+"."+"buildtime")){
            System.out.println("BOZO this nay exist  "+name);
        }
        float ret=u(name,"buildtime");
        return ret;
    }
    private static float ud(String currentItem,String stat,float defalt){
        return unitStats.getOrDefault(currentItem+"."+ stat.toLowerCase(),defalt);
    }
    private static float u(String currentItem,String stat) {
        return unitStats.get(currentItem+"."+ stat.toLowerCase());
    }
    private static boolean cu(String currentItem,String stat){
        return unitStats.containsKey(currentItem+"."+ stat.toLowerCase());
    }


}
