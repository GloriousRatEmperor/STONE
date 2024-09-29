package components;

import java.util.ArrayList;
import java.util.List;

public class DamagePart {
    public float baseDamage;
    public List<String> types=new ArrayList<>();
    private transient ArrayList<Modifier> modifiers=new ArrayList<>();
    public float tempMod;
    public void updateTemps(float time){
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
    public DamagePart(float dmg){
        baseDamage=dmg;
    }
    public DamagePart(float dmg,String type){
        baseDamage=dmg;
        types.add(type);
    }


}
