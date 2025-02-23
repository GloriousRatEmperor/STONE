package components.SubComponents.Commands;

import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;

import java.util.List;

public class MoveCommand extends Command{
    public Transform transform;
    public Vector2f pos;
    protected MoveContollable move;
    protected boolean amove=false;
    public float tolerance=-1;
    public MoveCommand(Transform t){
        transform=t;
        pos=null;
    }
    public MoveCommand(Vector2f p){
        pos=new Vector2f(p);;
        transform=null;
    }

    public MoveCommand(Transform t,float tolerance){
        transform=t;
        pos=null;
        this.tolerance=tolerance;
    }

    public MoveCommand(Vector2f p,float tolerance){
        pos=new Vector2f(p);;
        transform=null;
        this.tolerance=tolerance;
    }
    public MoveCommand(Transform t,float tolerance,boolean amove){
        transform=t;
        pos=null;
        this.tolerance=tolerance;
        this.amove=amove;

    }

    public MoveCommand(Vector2f p,boolean amove){
        pos=new Vector2f(p);
        transform=null;
        this.amove=amove;
    }
    public MoveCommand(List<Float> p){
        pos=new Vector2f(p.get(0),p.get(1));
        transform=null;
    }
    @Override
    public void update(float dt,GameObject self){
        move.move(dt);
    }
    @Override
    public void kill(){
        super.kill();
        if(move!=null){
            if(move.trigger==this){
                move.trigger=null;
                move.stop(false);
            }
        }
        transform=null;
        pos=null;
        move=null;
    }
    @Override
    public void apply(GameObject self) {
        if(amove){
            brain.setAfk(true);
        }
        this.move=self.getComponent(MoveContollable.class);
        if (transform!=null) {
            self.getComponent(MoveContollable.class).moveCommand(transform, this,tolerance);
        }else{
            self.getComponent(MoveContollable.class).moveCommand(pos, this,tolerance);
        }
    }


}
