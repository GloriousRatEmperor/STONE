package components.SubComponents.Abilities;

import components.SubComponents.Effects.AlternatorMarked;
import components.unitcapabilities.defaults.Effects;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;
import util.Img;

public class TeleportMarked extends Ability{

    public TeleportMarked(AbilityName type) {
        super(type);
        sprite= Img.get("alteratormark");
        updateDesc();
        requiresTarget=true;
        targetable=true;
        this.mp=25;
    }


    @Override
    public void updateDesc(){
        setDesc("teleports to a marked enemy, removing the mark, (you can tp several alternators with one mark at once with ctrl)");
    }
    @Override
    public boolean cast(final Vector2f pos, GameObject self, GameObject target) {
        Effects effects=(target.getComponent(Effects.class));
        if(effects==null){
            return false;
        }
        AlternatorMarked mark=effects.getSubComponent(AlternatorMarked.class);
        if(mark!=null){
            mark.durationNow=0.5f;
        }else{
            return false;
        }

        self.getComponent(Rigidbody2D.class).setPosition(target.transform.position);
        self.transform.position.set(target.transform.position);

        return true;
    }
}
