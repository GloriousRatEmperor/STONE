package components;

import util.UniTime;

import java.util.ArrayList;
import java.util.Objects;

public class Damage {
    private transient ArrayList<DamagePart> damage=new ArrayList<>();
    private transient ArrayList<Modifier> globalModifiers=new ArrayList<>();
    private transient int globalModtemp;
    private transient float lastUsed= UniTime.getFrame();
    float allDmg;
    public Damage(float baseDamage){
        this.damage.add(new DamagePart(baseDamage));
        allDmg=allDamage();
    }
    public Damage(float baseDamage,String type){
        this.damage.add(new DamagePart(baseDamage,type));
        allDmg=allDamage();
    }
    public void setBaseDamage(float dmg){
        damage.get(0).baseDamage=dmg;
    }
    public float allDamage(){
        float allDamage=0;
        for(DamagePart d:damage){
            allDamage+=d.baseDamage*d.tempMod;
        }
        allDmg=allDamage;
        return allDamage*globalModtemp;
    }
    public void updateTemps(){
        lastUsed=UniTime.getFrame();
        int i=0;
        while( i<globalModifiers.size()) {
            Modifier mod=globalModifiers.get(i);
            if(mod.duration<lastUsed){
                globalModifiers.remove(mod);
                continue;
            }
            globalModtemp*=mod.mult;
            i++;
        }
        for (DamagePart p:damage) {
            p.updateTemps(lastUsed);
        }
    }
    public void addGlobalMult(float mult,String type,float duration){
        globalModifiers.add(new Modifier(mult,type,duration));
    }
    public void addGlobalMult(float mult,String type){
        globalModifiers.add(new Modifier(mult,type,0));
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
