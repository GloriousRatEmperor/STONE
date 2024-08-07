package physics2d.components;

import components.Component;
import jade.GameObject;
import jade.Transform;
import jade.Window;
import org.joml.Vector2f;
import util.Maf;

import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MoveContollable extends Component {

    public MoveContollable Clone(){
        MoveContollable c=new MoveContollable();
        c.speed=speed;
        return c;
    }
    private boolean first=true;
    public boolean targeted(){
        return target!=null;
    }
    private Transform target=null;
    public int speed=1;
    private double lastmove=10000000;
    public int timeoutafter=100;
    private int Bigtimer=90000;
    private float closeenuf=0;

    public int cannaygetclosertimeout=0;
    private Vector2f movePos=new Vector2f();
    @Override
    public void update(float dt){

        if(cannaygetclosertimeout>0){
            GameObject self=super.gameObject;
            Rigidbody2D body=self.getComponent(Rigidbody2D.class);
            double distance=move();
            if(distance>=lastmove){
                cannaygetclosertimeout--;
                Bigtimer--;

                if(timeout()||distance/dt<=body.getSpeed()*3+closeenuf/dt){
                    stop(distance/dt<=body.getSpeed()*3+closeenuf/dt);
                }
            }
            lastmove=distance;
        }
    }
    private Boolean timeout(){
            return cannaygetclosertimeout==0||Bigtimer==0;
    }
    public double move(){

        if(targeted()){
            movePos=target.position;
            lastmove+= Maf.distance(target.position, movePos);
        }

        GameObject self=super.gameObject;
        Rigidbody2D body=self.getComponent(Rigidbody2D.class);


        if(body!=null){

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
                };
                spdy = spdx * yS / xS;
            };

            body.setVelocity(new Vector2f((float) spdx, (float) spdy));
            double distance=Math.sqrt( Math.pow(xS,2)+Math.pow(yS,2));
            if(first){

                self.transform.setFlippedX(spdx<0);
                first=false;
            }
            return distance;
        }
        return 0f;
    }

    public void moveCommand(List<Float> pos, GameObject self){
        closeenuf=0;

        this.target=null;
        this.movePos=new Vector2f(pos.get(0), pos.get(1));
        Bigtimer= (int) (Maf.distance(movePos,self.transform.position)/speed/ Window.physicsStep*1.5);
        SharedMoveCommand();
    }
    public void moveCommand(Transform target){
        this.target=target;
        CircleCollider circle=target.gameObject.getComponent(CircleCollider.class);
        CircleCollider mecircle=target.gameObject.getComponent(CircleCollider.class);
        float merad;
        if(mecircle!=null){
            merad=mecircle.getRadius();
        }else{
            merad=gameObject.transform.scale.x / 4+gameObject.transform.scale.y / 4;
        }
        if(circle!=null){
            closeenuf= circle.getRadius()+merad;
        }
        else {
            closeenuf = target.scale.x / 4 + target.scale.y / 4+merad;
        }
        this.movePos=target.position;
        SharedMoveCommand();

    }
    public void SharedMoveCommand(){
        first=true;
        Rigidbody2D body=gameObject.getComponent(Rigidbody2D.class);
        body.setLinearDamping(body.moveDamping);

        lastmove=1000000;
        cannaygetclosertimeout=timeoutafter;
    }

    public void stop(Boolean gotthere){

        cannaygetclosertimeout=0;
        Rigidbody2D body=super.gameObject.getComponent(Rigidbody2D.class);

        if(body!=null){
            body.setLinearDamping(body.stopDamping);
            body.setVelocity(new Vector2f());
        }
        if(targeted()) {
            Transform temp=target;
            this.target=null;

            if(gotthere) {
                super.gameObject.Interact(temp.gameObject);
            }
        }

    }

}
