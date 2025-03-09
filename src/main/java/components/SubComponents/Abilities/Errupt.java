package components.SubComponents.Abilities;

import components.SubComponents.Frame.GrowSpinFrame;
import components.unitcapabilities.Animation;
import components.unitcapabilities.damage.Mortal;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;
import util.Unit;

import java.util.ArrayList;

import static jade.Window.getPhysics;

public class Errupt extends Ability{
    public float damage;
    public float radius;
    public Animation animation;
    public Errupt(AbilityName type) {
        super(type);
        sprite = Img.get("errupt");
        updateDesc();
    }
    @Override
    public void updateDesc(){
        setDesc("explodes on death for "+Math.round(damage)+" damage"+" in a radius of "+Math.round( radius)+" but like with a cooler effect than explodeOnDeathEffect can be triggered early");
    }
    @Override
    public void die(GameObject self){
        explode();
        Vector2f trans=self.transform.position;
        makePuddle(new Vector2f( trans.x-radius,trans.y),self.allied);
        makePuddle(new Vector2f( trans.x+radius,trans.y),self.allied);
        makePuddle(new Vector2f( trans.x,trans.y-radius),self.allied);
        makePuddle(new Vector2f( trans.x,trans.y+radius),self.allied);

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
        GrowSpinFrame frame =new GrowSpinFrame( Img.get("errupt"), 0.25f,  7.3f, 0);
        frame.growX=1.3f;
        explode.addFrame(frame);

        //}
        Unit.generateAnimation(owner.gameObject.transform.position, radius*0.75f,radius*0.3f,explode);


    }
    public void makePuddle(Vector2f pos, int allied){
        Unit.make("puddle",pos,allied);
    }
    @Override
    public void cast(final Vector2f pos, GameObject self){
        self.die(self);
    }



}
