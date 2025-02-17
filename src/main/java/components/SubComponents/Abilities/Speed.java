package components.SubComponents.Abilities;

import components.SubComponents.Effects.SpeedUp;
import components.unitcapabilities.defaults.Effects;
import enums.AbilityName;
import enums.EffectName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;
import util.UniTime;

public class Speed extends Ability{

    private float mult=3f;
    public float lastActive;
    public float duration=10;
    private float keepSpeedPercent=0;
    @Override
    public Speed Copy(){
        Speed speed=new Speed(type);
        speed.mult=mult;
        speed.lastActive=lastActive;
        speed.keepSpeedPercent=keepSpeedPercent;
        return speed;
    }

    public Speed(AbilityName type) {
        super(type);
        mp=0;
        sprite = Img.get("speed");
        lastActive=-duration;

    }
    @Override
    public void updateDesc(){
        setDesc("Incrases speed by "+ (Math.round(mult*100)-100)+"%% " +
                "for " +(int)duration+" ...timeunits (about 0.89 seconds unless it's friday)");
    }
    @Override
    public void cast(final Vector2f pos, GameObject self) {

        this.lastActive= UniTime.getFrame();
        Effects effects=self.getComponent(Effects.class);
        SpeedUp speed=(SpeedUp) effects.getEffect(EffectName.speedUp,duration,mult);
        speed.keepSpeedPercent=keepSpeedPercent;
        effects.addEffect(speed);

    }

    @Override
    public boolean Castable(float mp) {
        return (mp>=super.mp& lastActive+duration<UniTime.getFrame());
    }
}
