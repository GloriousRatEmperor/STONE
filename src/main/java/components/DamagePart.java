package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamagePart {
    public float baseDamage;
    public boolean magic=false;
    public List<String> types=new ArrayList<>();
    private transient ArrayList<Modifier> modifiers=new ArrayList<>();
    public float tempMod;
    public float tempDmg;
    public void updateMods(float time){
        tempMod=1;
        int i=0;
        while( i<modifiers.size()) {
            Modifier mod=modifiers.get(i);
            if(mod.duration<time){

                modifiers.remove(mod);
                continue;
            }
            tempMod*=mod.mult;
            i++;
        }

    }
    public void resetTempDamage(){
        tempDmg=baseDamage;
    }
    public void resistMod(String type,float resistance) { //makes the bonus(or benefit) resistance times smaller, resistance 0.5 also makes it twice as big
        for (Modifier mod : modifiers) {
            if (Objects.equals(mod.type, type)) {
                if (mod.tempMult != 1 & mod.tempMult != 0) {
                    mod.tempMult = (mod.tempMult - 1) / resistance + 1;
                }
            }
        }
    }
    public void resistType(String type,float resistance){ //makes damage resistance times smaller (assuming it has the type), resistance 0.5 also makes it twice as big
        for(String typ:types){
            if(Objects.equals(typ, type)){
                tempMod/=resistance;
            }
        }
    }
    public DamagePart(float dmg){
        baseDamage=dmg;
        tempDmg=dmg;
    }
    public DamagePart(float dmg,String type){
        baseDamage=dmg;
        tempDmg=dmg;
        types.add(type);
    }


}
