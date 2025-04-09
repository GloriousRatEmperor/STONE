package components.SubComponents.Effects;

import components.SubComponents.Animation.Animation;
import jade.GameObject;
import util.Img;

public class ExplodingProjectiles extends ImbuneEffect{
    public float damage;
    public float radius;
    public Animation animation;

    public ExplodingProjectiles(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("rock");
        radius=power;
        damage=power*40;
        updateDesc();
    }


    @Override
    public void updateDesc(){
        setDesc("adds an effect to this thing's projectiles that explode on death for "+Math.round(damage)+" damage in a radius of "+Math.round( radius));
    }
    private Animation cloneAnimationState(){
        if(animation!=null){
            return new Animation(animation);
        }else{
            return null;
        }
    }
    @Override
    public Effect imbune(GameObject parent){
        ExplodeOnDeath e=new ExplodeOnDeath(durationTotal,power);
        e.damage=damage;
        e.radius=radius;
        e.animation=cloneAnimationState();
        return e;
    }
}
