package components.SubComponents.Effects;

import components.SubComponents.Animation.Animation;
import components.unitcapabilities.damage.Mortal;
import jade.GameObject;
import util.Img;
import util.UnitUtils.CreatorTools;

import java.util.ArrayList;

import static jade.Window.getPhysics;

public class ExplodeOnDeath extends Effect {
    public float damage;
    public float radius;
    public Animation animation;//FUCK but also FUCK YEA but also FUCK NO
    public ExplodeOnDeath(float duration, float power) {
        super(duration, power);
        this.sprite=Img.get("rock");
        type="";
        damage=power*40;
        radius=power;
        updateDesc();
    }
    @Override
    public void updateDesc(){
        setDesc("explodes on death for "+Math.round(damage)+" damage"+" in a radius of "+Math.round( radius));
    }
    @Override
    public void die(GameObject self){
        explode();
    }
    public void explode(){
        ArrayList<GameObject> explodedObjects= getPhysics().getGameObjectsTouchingCircle(owner.gameObject.transform.position,radius);
        for(GameObject go:explodedObjects){
            Mortal mort=go.getComponent(Mortal.class);
            if(mort!=null){
                mort.takeDamage(damage);
            }
        }
        Animation explode;
//        if(animation!=null){
//            explode = animation;
//        }else {
            explode = new Animation();
            explode.addFrame(Img.get("explod"), 0.2f, radius * 2 * 10, 2000);
        //}
        CreatorTools.generateAnimation(owner.gameObject.transform.position, 0.1f,0.1f,explode);


    }

}
