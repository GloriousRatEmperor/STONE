package components.SubComponents.Effects;

import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;
import util.Img;

public class SpeedUp extends Effect{
    public float keepSpeedPercent;

    public SpeedUp(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("rock");

        type="Buff";
        updateDesc();
    }
    @Override
    public void apply(GameObject self){
        MoveContollable movement = self.getComponent(MoveContollable.class);
        if (movement != null) {
            movement.speed *= power;
        }
        Rigidbody2D currentMove = self.getComponent(Rigidbody2D.class);
        if (currentMove != null) {
            currentMove.setVelocity(new Vector2f(currentMove.getVelocity().x*power,currentMove.getVelocity().y*power));

        }
    }
    @Override
    public void updateDesc(){
        setDesc("sped up by"+(power-1)*100+" %%");
    }
    @Override
    public void expire(GameObject self){
        float slowMult=1/power +keepSpeedPercent/100*(1-1/power);
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
