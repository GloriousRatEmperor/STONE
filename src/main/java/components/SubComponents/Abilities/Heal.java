package components.SubComponents.Abilities;

import components.unitcapabilities.damage.Mortal;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;

public class Heal extends Ability{
    private float power=0.5f;
    @Override
    public Heal Copy(){
        Heal heal=new Heal(type);
        return heal;
    }
    public Heal(AbilityName type) {
        super(type);
        mp=50;
        sprite = Img.get("heal");


    }
    @Override
    public boolean consider(GameObject self,boolean mpcapped){
        Mortal mort=self.getComponent(Mortal.class);
        if(mort!=null){
            if(mort.health<=mort.maxHealth/3||(mpcapped&&mort.health<=mort.maxHealth/1.5f)){
                return true;
            }
        }
        return false;
    }
    @Override
    public void updateDesc(){
        setDesc("restores "+ Math.round(power*100)+"%% missing hp");
    }
    @Override
    public boolean cast(final Vector2f pos, GameObject self,GameObject target) {
        Mortal mortal=self.getComponent(Mortal.class);
        if(mortal!=null){
            mortal.health+= power*(mortal.maxHealth-mortal.health);
            return true;
        }else{
            return false;
        }
}
}
