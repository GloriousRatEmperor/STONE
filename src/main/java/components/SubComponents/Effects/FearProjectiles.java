package components.SubComponents.Effects;

import jade.GameObject;
import org.joml.Vector2f;
import util.Img;

public class FearProjectiles extends ImbuneEffect{

    public FearProjectiles(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("skull");
        updateDesc();
    }


    @Override
    public void updateDesc(){
        setDesc("projectiles cause fear for "+Math.round(power*10)/10f+" seconds");
    }
    @Override
    public Effect imbune(GameObject parent){
        ImbuneFear fear=new ImbuneFear(Float.MAX_VALUE,power);
        fear.pos=new Vector2f(parent.transform.position);
        return fear;
    }
}