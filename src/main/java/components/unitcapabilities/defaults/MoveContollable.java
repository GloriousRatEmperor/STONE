package components.unitcapabilities.defaults;

import components.Component;
import jade.GameObject;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;

import static java.lang.Math.*;

public class MoveContollable extends Component {
    public float speed=5;
    public double turn=0; //what percentage of speed can be moved in the correct direction every frame with 1 being 100%, only works on non-1 acceleration
    public double acceleration=1;//what percentage of max speed can it can get in a frame with 1 being 100% (technically 1 is not just 100% but rather instant total acceleration ignoring previous speed)

    public void reverse(GameObject self){
        Rigidbody2D body=self.getComponent(Rigidbody2D.class);
        body.setVelocity(-body.getVelocity().x,-body.getVelocity().y);
    }
    public void prepareMove(){
    Rigidbody2D body=gameObject.getComponent(Rigidbody2D.class);
        body.setLinearDamping(body.moveDamping);
    }


    public double moveToward(float dt,Vector2f pos){ //returns distance
        GameObject self=super.gameObject;
        Rigidbody2D body=self.getComponent(Rigidbody2D.class);



        double xS = (pos.get(0)) - (self.transform.position.x);
        double yS = (pos.get(1)) - (self.transform.position.y);

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
    public double moveAway(float dt,Vector2f pos){ //returns distance
        Vector2f target=new Vector2f(super.gameObject.transform.position.x*2-pos.x,super.gameObject.transform.position.y*2-pos.y);
        return moveToward(dt,target);


    }


    public void halt(boolean stopdamping){
        Rigidbody2D body = super.gameObject.getComponent(Rigidbody2D.class);
        if (body != null) {
            if(stopdamping){
                body.setLinearDamping(body.stopDamping);
            }

            body.setVelocity(new Vector2f());
        }
    }

}
