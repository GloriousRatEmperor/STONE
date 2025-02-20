package components.SubComponents.Commands;

import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import jade.Transform;
import util.Maf;

public class SkirmishCommand extends MoveCommand {
    protected float maxRange=3f;
    protected float minRange=2.8f;

    public SkirmishCommand(Transform t,float minrange,float maxRange){
        super(t);
        this.maxRange=maxRange;
        this.minRange=minrange;
    }
    public SkirmishCommand(Transform t,float tolerance,boolean amove,float minrange,float maxRange){
        super(t,tolerance,amove);
        this.maxRange=maxRange;
        this.minRange=minrange;

    }

    @Override
    public void apply(GameObject self) {
        if(amove){
            brain.setAfk(true);
        }
        this.move=self.getComponent(MoveContollable.class);
        self.getComponent(MoveContollable.class).moveCommand(transform, this, tolerance);


    }
    @Override
    public void update(float dt,GameObject self){
        double distance=Maf.distance(transform.position,self.transform.position);
        if(distance>maxRange){
            move.move(dt);
        }else if(Maf.distance(transform.position,self.transform.position)<minRange){
            move.move(dt);
            move.reverse(self);
        }
    }

}
