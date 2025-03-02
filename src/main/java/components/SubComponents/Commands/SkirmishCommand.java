package components.SubComponents.Commands;

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
    public SkirmishCommand(Transform t,boolean amove,float minrange,float maxRange){
        super(t);
        this.amove=amove;
        this.maxRange=maxRange;
        this.minRange=minrange;

    }

    @Override
    public void update(float dt,GameObject self){
        if(!validTransform()){
            done();
        }
        double distance= Maf.distance(transform.position,self.transform.position);
        double dist;
        if(distance<minRange){
            dist=move.moveAway(dt,transform.position);
        }else if(distance<maxRange){
            dist=Maf.distance(transform.position,self.transform.position);
            move.halt(false);
        }else{
            dist=move.moveToward(dt,transform.position);
        }

        Bigtimer--;
        if(Bigtimer==0||dist<0) {
            done();
        }

    }

}
