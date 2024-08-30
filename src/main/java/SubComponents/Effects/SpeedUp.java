package SubComponents.Effects;

import jade.GameObject;
import org.joml.Vector2f;
import components.MoveContollable;
import physics2d.components.Rigidbody2D;

public class SpeedUp extends Effect{
    public float keepSpeedPercent;

    public SpeedUp(float duration, float power) {
        super(duration, power);
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
