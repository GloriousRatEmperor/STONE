package components.unitcapabilities.damage;

import util.UniTime;

import java.util.ArrayList;
import java.util.Objects;

public class Damage {
    private transient ArrayList<DamagePart> damage=new ArrayList<>();
    private transient ArrayList<Modifier> globalModifiers=new ArrayList<>();
    private transient int globalModtemp=1;
    private transient float lastUsed= UniTime.getFrame();
    float allDmg;
    float physical;
    public int owner=-1;
    float magical;
    public Damage(float baseDamage){
        this.damage.add(new DamagePart(baseDamage));
        calcAllDamage();

    }
    public Damage(float baseDamage,String type){

        this.damage.add(new DamagePart(baseDamage,type));
        calcDamage();
    }
    public Damage(float baseDamage,float magicPercent){
        if(magicPercent!=100){
            this.damage.add(new DamagePart(baseDamage*(100-magicPercent)/100,false));
        }
        if(magicPercent!=0){
            this.damage.add(new DamagePart(baseDamage*(magicPercent)/100,true));
        }
        calcDamage();
    }
    public void setBaseDamage(float dmg){
        damage.get(0).baseDamage=dmg;
        calcDamage();
    }
    public void calcAllDamage(){
        calcMod();
        calcDamage();
        mergeDamage();
    }
    public void calcDamage(){
        physical=0;
        magical=0;
        for(DamagePart d:damage){
            if(d.magic){
                magical+=d.tempDmg*d.tempMod*globalModtemp;

            }else {
                physical += d.tempDmg * d.tempMod*globalModtemp;
            }
            d.resetTempDamage();
        }

    }
    public void mergeDamage(){
        allDmg=magical+physical;
    }
    public float getDamage(){
        return allDmg;
    }
    public void resistMod(String type,float resistance){ //makes the bonus(or benefit) resistance times smaller, resistance 0.5 also makes it twice as big
        for(Modifier mod:globalModifiers){
            if(Objects.equals(mod.type, type)){
                if(mod.tempMult!=1&mod.tempMult!=0) {
                    mod.tempMult = (mod.tempMult - 1) / resistance + 1;
                }
            }
        }
        for (DamagePart dmg:damage
             ) {
            dmg.resistMod(type, resistance);
        }
    }
    public void resistPhysical(float armor){
        if(physical!=0) {
            physical = physical / (1 + (armor / physical));

        }
    }
    public void resistMagic(float armor){
        if(magical!=0) {
            magical = magical / (1 + (armor / magical));
        }
    }

    public void calcMod(){
        lastUsed=UniTime.getFrame();
        int i=0;
        globalModtemp=1;
        while( i<globalModifiers.size()) {
            Modifier mod=globalModifiers.get(i);
            if(mod.duration<lastUsed){
                globalModifiers.remove(mod);
                continue;
            }
            globalModtemp*=mod.tempMult;
            mod.updateTemp();
            i++;
        }
        for (DamagePart dmg:damage) {
            dmg.updateMods(lastUsed);
        }

    }
    public void resistType(String type,float resistance){ //makes the bonus(or benefit) resistance times smaller, resistance 0.5 also makes it twice as big
        for(DamagePart dmg:damage){
            dmg.resistType( type,resistance);
        }
    }
    public float getCurrentMult(){
        return globalModtemp;
    }
    public void addGlobalMult(float mult,String type,float duration){
        globalModifiers.add(new Modifier(mult,type,duration));
    }
    public void addGlobalMult(float mult,String type){
        globalModifiers.add(new Modifier(mult,type,0));
    }
    public void addGlobalMult(Modifier mod){
        globalModifiers.add(mod);
    }
    public void removeAllGlobalMultOfType(String type){
        for(Modifier mod:globalModifiers){
            if(Objects.equals(mod.type, type)){
                globalModifiers.remove(mod);
                return;
            }
        }
    }


}
