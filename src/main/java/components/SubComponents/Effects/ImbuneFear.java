package components.SubComponents.Effects;

import jade.GameObject;
import org.joml.Vector2f;
import util.Img;

public class ImbuneFear extends ImbuneEffect{
    Vector2f pos;
    public ImbuneFear(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("skull");
        updateDesc();
    }


    @Override
    public void updateDesc(){
        setDesc("causes hit units to run away in fear for "+Math.round(power*10)/10f+" seconds");
    }

    @Override
    public Effect imbune(GameObject parent){
        Fear fear=new Fear(power,power);
        fear.direction=this.pos;
        return fear;
    }
}
