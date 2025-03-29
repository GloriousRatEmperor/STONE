package components.SubComponents.Commands;

import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;

import java.util.List;

public class MoveCommand extends Command{
    public Transform transform;
    public Vector2f pos;

    public double lastdistance=10000000;
    public int Bigtimer=90000;
    public float closeenuf=0;
    protected transient MoveContollable move;
    public boolean amove=false;
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
        double dist=move(dt);
        Bigtimer--;
        if(Bigtimer==0||dist<0) {
            done();
        }
        if(dist<closeenuf+move.speed*dt){

            done();
            if(transform!=null){
                self.Interact(transform.gameObject);
            }
        }
        lastdistance=dist;

    }
    @Override
    public void done(){
        move.stopDamping();
        brain.notifyDone();
    }
    boolean validTransform(){
        return !transform.gameObject.isDead();
    }
    public double move(float dt){
        if(transform!=null){
            if(!validTransform()){
                return -1;
            }
            return move.moveToward(dt,transform.position);
        }else{
            return move.moveToward(dt,pos);
        }
    }

    @Override
    public void apply(GameObject self) {
        if(amove){
            brain.setAfk(true);
        }
        this.move=self.getComponent(MoveContollable.class);
        if (transform!=null) {
            prepareMove(transform,self,tolerance);
        }else{

            prepareMove(pos,tolerance);
        }
    }

    public void prepareMove(Vector2f pos,float tolerance){

        if(tolerance==-1) {
            closeenuf = 0;
        }else{
            closeenuf=tolerance;
        }

        SharedPrepareMove();

    }
    public void prepareMove(Transform target,GameObject self,float tolerance){
        self.startMove(target);
        CircleCollider circle=self.getComponent(CircleCollider.class);
        CircleCollider mecircle=target.gameObject.getComponent(CircleCollider.class);
        if(tolerance!=-1) {
            closeenuf = tolerance;
        }else{
            float merad;
            if(mecircle!=null){
                merad=mecircle.getRadius();
            }else{
                merad=self.transform.scale.x / 2f+self.transform.scale.y / 2f;
            }

            if (circle != null) {
                closeenuf = circle.getRadius() + merad;
            } else {
                closeenuf = target.scale.x / 2f + target.scale.y / 2f + merad;
            }

        }

        SharedPrepareMove();

    }

    public void SharedPrepareMove(){

        move.prepareMove();
        Bigtimer=90000;
        lastdistance=1000000;

    }



    @Override
    public void kill(){
        super.kill();
        move.stopDamping();
        move=null;
    }
    @Override
    public void destroy(){
        transform=null;
        pos=null;
        move=null;
    }


}
