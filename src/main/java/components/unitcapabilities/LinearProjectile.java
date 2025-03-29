package components.unitcapabilities;

import jade.GameObject;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;

import static java.lang.Math.*;
import static java.lang.Math.abs;

public class LinearProjectile extends Projectile{
    public Vector2f movePos;
    public double maxSpdx =0;
    public double maxSpdy =0;
    public LinearProjectile(float damage, float magicPercent) {
        super(damage, magicPercent);
        guided=false;
    }
    public LinearProjectile(float damage){
        super(damage);
        guided=false;
    }

    public void prepMoveCommand(Vector2f pos){

        this.movePos=pos;
        super.SharedMoveCommand(null);
    }
    @Override
    public void update(float dt) {
        lifespan-=dt;
        if(lifespan>0) {
            addSpeed();
            if (nextSmak >= 0) {
                nextSmak -= dt;
                if (nextSmak <= 0) {
                    rb.setNotSensor();
                }

            }
        }else {
            death();
        }
    }
    public void addSpeed(){
        GameObject self=super.gameObject;
        Rigidbody2D body=self.getComponent(Rigidbody2D.class);
        double spdx,spdy;
        spdx=maxSpdx;
        spdy=maxSpdy;

        if(acceleration!=1){


            Vector2f nowsped = body.getVelocity();

            double diff= maxSpdx-nowsped.x;
            double limit=abs(acceleration*maxSpdx);
            if(diff>limit){
                spdx=nowsped.x+limit;
            } else if (diff<-limit) {
                spdx=nowsped.x-limit;
            }

            diff=maxSpdy-nowsped.y;
            limit=abs(acceleration*maxSpdy);
            if(diff>limit){
                spdy=nowsped.y+limit;

            }else if(diff<-limit){
                spdy=nowsped.y-limit;
            }
        }

        body.setVelocity((float) spdx, (float) spdy);

    }
    @Override
    public double moveCommand(){

        GameObject self=super.gameObject;
        Rigidbody2D body=self.getComponent(Rigidbody2D.class);


        double xS = (movePos.get(0)) - (self.transform.position.x);
        double yS = (movePos.get(1)) - (self.transform.position.y);

        if (xS == 0) {
            maxSpdx = speed;
            maxSpdy = 0;
        }else {
            maxSpdx = speed / sqrt(pow(yS , 2) / pow(xS,2) + 1);

            if (xS < 0) {
                maxSpdx *= -1;
            }
            maxSpdy = maxSpdx * yS / xS;
        }
        self.transform.rotation= (float) ((atan(maxSpdy / maxSpdx))/(2*Math.PI)*360);
        if(first){

            self.transform.setFlippedX(maxSpdx <0);

            first=false;
        }

        body.setVelocity((float) maxSpdx, (float) maxSpdy);
        double distance=Math.sqrt( Math.pow(xS,2)+Math.pow(yS,2));

        return distance;
    }



}
