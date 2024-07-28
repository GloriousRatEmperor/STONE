package Abilitiess;

import Multiplayer.ServerData;
import components.Effects;
import components.Spritesheet;
import jade.GameObject;
import util.AssetPool;
import util.UniTime;
import Effects.SpeedUp;

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
        Spritesheet AbilitySprites = AssetPool.getSpritesheet("assets/images/abilities.png");
        sprite = AbilitySprites.getSprite(1);
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
        System.out.println(lastActive);
        System.out.println(UniTime.getFrame());
        System.out.println("-------------");
        return (mp>=super.mp& lastActive+duration<UniTime.getFrame());
    }
}
