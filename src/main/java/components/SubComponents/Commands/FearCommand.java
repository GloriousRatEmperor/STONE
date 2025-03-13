package components.SubComponents.Commands;

import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import org.joml.Vector2f;

public class FearCommand extends MoveCommand{
    public float duration;
    public FearCommand(Vector2f pos) {
        super(pos);
    }
    @Override
    public void apply(GameObject self){
        this.move=self.getComponent(MoveContollable.class);
        if(move==null){
            brain.notifyDone();
            return;
        }
        if (transform!=null) {
            prepareMove(transform,self,tolerance);
        }else{
            prepareMove(pos,tolerance);
        }
    }
    @Override
    public void update(float dt, GameObject self){
        move.moveAway(dt,pos);
        if(brain.stunTimer<0) {
            done();
        }

    }

}
