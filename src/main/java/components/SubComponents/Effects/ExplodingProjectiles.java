package components.SubComponents.Effects;

import components.unitcapabilities.AnimationState;
import util.Img;

public class ExplodingProjectiles extends ImbuneEffect{
    public float damage;
    public float radius;
    public AnimationState animation;

    public ExplodingProjectiles(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("rock");
        type="ImbuneProjectiles";
        radius=power;
        damage=power*40;
        updateDesc();
    }


    @Override
    public void updateDesc(){
        setDesc("adds an effect to this thing's projectiles that explode on death for "+Math.round(damage)+" damage in a radius of "+Math.round( radius));
    }
    private AnimationState cloneAnimationState(){
        if(animation!=null){
            return new AnimationState(animation);
        }else{
            return null;
        }
    }
    @Override
    public Effect imbune(){
        ExplodeOnDeath e=new ExplodeOnDeath(durationTotal,power);
        e.damage=damage;
        e.radius=radius;
        e.animation=cloneAnimationState();
        return e;
    }
}
