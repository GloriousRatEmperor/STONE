package components.SubComponents.Abilities;

import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;
import util.UnitUtils.ProjectileCreator;

public class Alterate extends Ability {


    public Alterate(AbilityName type,float duration,float power) {
        super(type);
        sprite = Img.get("alteratormark");
        updateDesc();
        targetable=true;
    }
    public Alterate(AbilityName type) {
        super(type);
        sprite = Img.get("alteratormark");
        updateDesc();
        targetable=true;
    }
    @Override
    public void updateDesc(){
        setDesc("does... nothing?");
    }
    @Override
    public boolean cast(final Vector2f pos, GameObject self, GameObject target){
        ProjectileCreator.makeProjectile("alterbolt",self.getUid(),self.transform.position,pos,self.allied);
        return true;
    }
}
