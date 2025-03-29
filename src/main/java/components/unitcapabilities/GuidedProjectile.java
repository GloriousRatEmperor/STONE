package components.unitcapabilities;

import components.unitcapabilities.damage.Mortal;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;

import static java.lang.Math.*;
import static java.lang.Math.abs;

public class GuidedProjectile extends Projectile{
    private float turn=0;
    public float tolerance;
    private Transform target=null;
    public GuidedProjectile(float damage, float magicPercent) {
        super(damage, magicPercent);
        guided=true;

    }
    public GuidedProjectile(float damage){
        super(damage);
        guided=true;
    }
    @Override
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        calcTolerance();
    }
    protected void calcTolerance(){
        tolerance=speed*1/60f;
        if(guided){
            GameObject enemy = target.gameObject;
            if (enemy != null) {
                CircleCollider circle=enemy.getComponent(CircleCollider.class);
                if(circle!=null){
                    tolerance+=circle.radius*0.8f;
                } else {
                    Box2DCollider box=enemy.getComponent(Box2DCollider.class);
                    if(box!=null){
                        Vector2f size=box.getHalfSize();
                        tolerance+=(size.x+size.y)*0.4f;
                    }
                }
            }
        }
    }

    public void prepMoveCommand(Transform target){
        this.target=target;
        super.SharedMoveCommand(target);
    }

    @Override
    public void update(float dt) {
        lifespan-=dt;
        if(lifespan>0) {
            if (nextSmak >= 0) {
                nextSmak -= dt;
                if (nextSmak <= 0) {
                    rb.setNotSensor();
                }

            }
            if (moveCommand() < tolerance) {
                GameObject enemy = target.gameObject;
                if (enemy != null) {
                    Mortal mort = target.gameObject.getComponent(Mortal.class);
                    if (mort != null) {
                        smak(mort);
                    } else {
                        death();
                    }
                } else {
                    death();
                }

            }
        }else {
            death();
        }
    }
    @Override
    public double moveCommand(){

        Vector2f movePos=target.position;

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
        self.transform.rotation= (float) ((atan(spdy/spdx))/(2*Math.PI)*360);
        self.transform.setFlippedX(spdx<0);

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




    @Override
    public void destroy(){
        this.target=null;
        this.rb=null;
    }
}
