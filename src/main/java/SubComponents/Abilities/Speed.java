package SubComponents.Abilities;

import Multiplayer.ServerData;
import SubComponents.Effects.SpeedUp;
import components.Effects;
import jade.GameObject;
import util.Img;
import util.UniTime;

public class Speed extends Ability{

    private float mult=3f;
    public float lastActive;
    public float duration=10;
    private float keepSpeedPercent=0;
    @Override
    public Speed Copy(){
        Speed speed=new Speed(id);
        speed.mult=mult;
        speed.lastActive=lastActive;
        speed.keepSpeedPercent=keepSpeedPercent;
        return speed;
    }

    public Speed( int id) {
        super(id);
        mp=0;
        sprite = Img.get("speed");
        setDesc("Incrases speed by "+ (Math.round(mult*100)-100)+"%% " +
                "for " +(int)duration+" ...timeunits (about 0.89 seconds unless it's friday)");
        lastActive=-duration;

    }
    @Override
    public void cast(ServerData data, GameObject self) {

        this.lastActive= UniTime.getFrame();
        SpeedUp speed=new SpeedUp(duration,mult);
        speed.keepSpeedPercent=keepSpeedPercent;
        self.getComponent(Effects.class).addEffect(speed);

    }

    @Override
    public boolean Castable(float mp) {
        return (mp>=super.mp& lastActive+duration<UniTime.getFrame());
    }
}
