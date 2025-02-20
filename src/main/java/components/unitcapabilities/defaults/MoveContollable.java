package components.unitcapabilities.defaults;

import components.Component;
import components.SubComponents.Commands.Command;
import jade.GameObject;
import jade.Transform;
import jade.Window;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import util.Maf;

import static java.lang.Math.*;

public class MoveContollable extends Component {

    private boolean first=true;
    public boolean targeted(){
        return target!=null;
    }
    private Transform target=null;
    public float speed=5;
    public double turn=0; //what percentage of speed can be moved in the correct direction every frame with 1 being 100%, only works on non-1 acceleration
    public double acceleration=1;//what percentage of max speed can it can get in a frame with 1 being 100% (technically 1 is not just 100% but rather instant total acceleration ignoring previous speed)
    private double lastmove=10000000;
    public int timeoutafter=20;
    private int Bigtimer=9000;
    private float closeenuf=0;
    public Command trigger;
    public int cannaygetclosertimeout=0;
    private Vector2f movePos=new Vector2f();
    public void move(float dt){
        if(cannaygetclosertimeout>0){
            GameObject self=super.gameObject;
            Rigidbody2D body=self.getComponent(Rigidbody2D.class);
            double distance= calcMove();
            if(distance==-1){
                return;
            }
            if(distance>=lastmove){
                cannaygetclosertimeout--;
                Bigtimer--;


            }
            if(timeout()||distance/dt<=body.getSpeed()+closeenuf/dt){
                stop(distance/dt<=body.getSpeed()+closeenuf/dt);
            }
            lastmove=distance;
        }
    }
    public void reverse(GameObject self){

        Rigidbody2D body=self.getComponent(Rigidbody2D.class);
        body.setVelocity(-body.getVelocity().x,-body.getVelocity().y);
    }
    private Boolean timeout(){
            return cannaygetclosertimeout==0||Bigtimer==0;
    }
    private double calcMove(){

        if(targeted()){
            if(target.gameObject.isDead()){
                stop(false);
                return -1;
            }
            movePos=target.position;
            lastmove+= Maf.distance(target.position, movePos);
        }

        GameObject self=super.gameObject;
        Rigidbody2D body=self.getComponent(Rigidbody2D.class);



            double xS = (movePos.get(0)) - (self.transform.position.x);
            double yS = (movePos.get(1)) - (self.transform.position.y);

            double spdx;
            double spdy;

            if (xS == 0) {
                spdx = speed;
                spdy = 0;
            }else {
                spdx = speed / sqrt(pow(yS , 2) / pow(xS,2) + 1);

                if (xS < 0) {
                    spdx *= -1;
                }
                spdy = spdx * yS / xS;
            }
            if(first){

                self.transform.setFlippedX(spdx<0);
                first=false;
            }
            if(acceleration!=1) {


                Vector2f nowsped = body.getVelocity();


                double turnamount=(body.getSpeed())*turn;
                double truespeed=Math.sqrt( Math.pow(spdx,2)+Math.pow(spdy,2));
                double turnX,turnY;
                turnX=turnamount*spdx/truespeed;
                turnY=turnamount*spdy/truespeed;
                turnX-= nowsped.x*turn;
                turnY-= nowsped.y*turn;



                double diff= spdx-nowsped.x;
                double limit=abs(acceleration*spdx);
                if(diff>limit){
                    spdx=nowsped.x+limit;
                } else if (diff<-limit) {
                    spdx=nowsped.x-limit;
                }
                diff=spdy-nowsped.y;
                limit=abs(acceleration*spdy);
                if(diff>limit){
                    spdy=nowsped.y+limit;

                }else if(diff<-limit){
                    spdy=nowsped.y-limit;
                }
                spdx+=turnX;
                spdy+=turnY;

            }

            body.setVelocity((float) spdx, (float) spdy);
            double distance=Math.sqrt( Math.pow(xS,2)+Math.pow(yS,2));

            return distance;

    }

    public void moveCommand(Vector2f pos,  Command trigger,float tolerance){
        this.trigger=trigger;
        if(tolerance==-1) {
            closeenuf = 0;
        }else{
            closeenuf=tolerance;
        }

        this.target=null;
        this.movePos=new Vector2f(pos.get(0), pos.get(1));
        Bigtimer= (int) (Maf.distance(movePos,gameObject.transform.position)/speed/ Window.physicsStep*1.5/Math.pow(acceleration,2));
        if(acceleration==1) {
            cannaygetclosertimeout = timeoutafter;
        }else{
            cannaygetclosertimeout = (int) (timeoutafter/Math.pow(acceleration,1/3f));
        }
        SharedMoveCommand();
    }
    public void moveCommand(Transform target, Command trigger,float tolerance){
        this.trigger=trigger;
        this.target=target;
        CircleCollider circle=gameObject.getComponent(CircleCollider.class);
        CircleCollider mecircle=target.gameObject.getComponent(CircleCollider.class);
        if(tolerance!=-1) {
            closeenuf = tolerance;
        }else{
            float merad;
            if(mecircle!=null){
                merad=mecircle.getRadius();
            }else{
                merad=gameObject.transform.scale.x / 2f+gameObject.transform.scale.y / 2f;
            }

            if (circle != null) {
                closeenuf = circle.getRadius() + merad;
            } else {
                closeenuf = target.scale.x / 2f + target.scale.y / 2f + merad;
            }

        }

        this.movePos=target.position;
        SharedMoveCommand();
        cannaygetclosertimeout=50000;
        Bigtimer=500000;
    }

    public void SharedMoveCommand(){
        first=true;
        Rigidbody2D body=gameObject.getComponent(Rigidbody2D.class);
        body.setLinearDamping(body.moveDamping);

        lastmove=1000000;



        super.gameObject.startMove(target);
    }

    public void stop(Boolean gotthere){

        if(targeted()&gotthere){
            if(!target.gameObject.isDead()) {
                if(this.target.gameObject.allied==super.gameObject.allied) {
                    super.gameObject.Interact(this.target.gameObject);
                }else {
                    return;
                }
            }
        }
        if(trigger!=null) {
            trigger.done();
        }
        cannaygetclosertimeout = 0;
        Rigidbody2D body = super.gameObject.getComponent(Rigidbody2D.class);

        if (body != null) {
            body.setLinearDamping(body.stopDamping);
            body.setVelocity(new Vector2f());
        }
        if (targeted()) {
            this.target = null;

        }


    }
    @Override
    public void destroy(){
        this.target=null;
    }

}
