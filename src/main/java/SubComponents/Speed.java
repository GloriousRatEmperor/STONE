package SubComponents;

import components.Spritesheet;
import enums.AbilityName;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2f;
import physics2d.components.MoveContollable;
import physics2d.components.Rigidbody2D;
import util.AssetPool;

import java.util.ArrayList;

public class Speed extends Ability{

    private float mult=2f;
    public boolean isActive=false;
    private float keepSpeedPercent=0;
    @Override
    public Speed Copy(){
        Speed speed=new Speed(this.name,id);
        speed.mult=mult;
        speed.isActive=isActive;
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
    public void Trigger(Vector2f position, ArrayList<Integer> id){
        Window.sendSelfcast(id,name);
    }
    @Override
    public void cast(Vector2f position, GameObject self) {
        this.isActive=true;
        MoveContollable movement = self.getComponent(MoveContollable.class);
        if (movement != null) {
            movement.speed *= mult;
        }
        Rigidbody2D currentMove = self.getComponent(Rigidbody2D.class);
        if (currentMove != null) {
            currentMove.setVelocity(new Vector2f(currentMove.getVelocity().x*mult,currentMove.getVelocity().y*mult));

        }

    }
    public void slowCast(Vector2f position, GameObject self) {
        this.isActive=false;
        float slowMult=1/mult +keepSpeedPercent/100*(1-1/mult);
        MoveContollable movement = self.getComponent(MoveContollable.class);
        if (movement != null) {
            movement.speed *= slowMult;
        }
        Rigidbody2D currentMove = self.getComponent(Rigidbody2D.class);
        if (currentMove != null) {
            currentMove.setVelocity(new Vector2f(currentMove.getVelocity().x*slowMult,currentMove.getVelocity().y*slowMult));

        }

    }
}
