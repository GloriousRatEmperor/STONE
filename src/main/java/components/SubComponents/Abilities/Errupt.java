package components.SubComponents.Abilities;

import components.SubComponents.Frame.Frame;
import components.SubComponents.Frame.FrameEffects.GrowSpinFrameEffect;
import components.unitcapabilities.Animation;
import components.unitcapabilities.damage.Mortal;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;
import util.Unit;

import java.util.ArrayList;

import static jade.Window.getPhysics;
import static jade.Window.getScene;

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
        makePuddle(new Vector2f( trans.x-radius*1.5f,trans.y),self.allied);
        makePuddle(new Vector2f( trans.x+radius*1.5f,trans.y),self.allied);
        makePuddle(new Vector2f( trans.x,trans.y-radius*1.5f),self.allied);
        makePuddle(new Vector2f( trans.x,trans.y+radius*1.5f),self.allied);

    }
    public void explode(){
        ArrayList<GameObject> explodedObjects= getPhysics().getGameObjectsTouchingCircle(owner.gameObject.transform.position,radius*1.5f);
        for(GameObject go:explodedObjects){
            Mortal mort=go.getComponent(Mortal.class);
            if(mort!=null){
                mort.takeDamage(damage);
            }
        }
        Animation explode;
        explode = new Animation();
        Frame frame=new Frame(Img.get("errupt"), 0.25f);
        GrowSpinFrameEffect effect= new GrowSpinFrameEffect(   7.3f, 0);
        effect.growX=1.3f;
        frame.addFrameEffect(effect);

        explode.addFrame(frame);

        Unit.generateAnimation(owner.gameObject.transform.position, radius*1.5f,radius*0.6f,explode);


    }
    public void makePuddle(Vector2f pos, int allied){
        GameObject puddle= Unit.makeMisc("puddle",pos,allied);
        getScene().addGameObjectToScene(puddle);

    }
    @Override
    public boolean cast(final Vector2f pos, GameObject self,GameObject target){
        self.die(self);
        return true;
    }



}
