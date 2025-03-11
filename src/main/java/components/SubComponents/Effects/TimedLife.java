package components.SubComponents.Effects;

import jade.GameObject;
import util.Img;

public class TimedLife extends Effect{


    public TimedLife(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("block3");
        type="R.I.P";
    }
    @Override
    public void updateDesc(){
        setDesc("dies when this expires");
    }
    @Override
    public void expire(GameObject self){
                self.die(self);
    }


}
