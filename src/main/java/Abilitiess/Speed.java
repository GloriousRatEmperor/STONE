package Abilitiess;

import Multiplayer.ServerData;
import components.Effects;
import components.Spritesheet;
import jade.GameObject;
import util.AssetPool;
import util.UniTime;
import Effects.SpeedUp;

public class Speed extends Ability{

    private float mult=2f;
    public float lastActive=-1;
    public float duration=100;
    private float keepSpeedPercent=0;
    @Override
    public Speed Copy(){
        Speed speed=new Speed(this.name,id);
        speed.mult=mult;
        speed.lastActive=lastActive;
        speed.keepSpeedPercent=keepSpeedPercent;
        return speed;
    }

    public Speed(String a, int id) {
        super(a,id);
        mp=20;
        Spritesheet AbilitySprites = AssetPool.getSpritesheet("assets/images/abilities.png");
        sprite = AbilitySprites.getSprite(1);
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
        return (mp>=super.mp& lastActive+duration>UniTime.getFrame());
    }
}
