package components;

import jade.GameObject;
import jade.Transform;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;

import java.util.List;

import static java.lang.Math.*;

public class Projectile extends Component {
    public float health=1;
    public float damage=100;
    public Boolean guided=false;
    public float attackSpeed=10;
    public float speed;
    private transient Rigidbody2D rb;
    public float nextSmak=0;
    private float turn=0;
    private Boolean first=true;
    private float lifespan=10;
    private Vector2f movePos=new Vector2f();
    public float acceleration=1;
    public boolean targeted(){
        return target!=null;
    }
    private Transform target=null;
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
    }

    @Override
    public Projectile Clone(){
        return new Projectile();
    }
    public void smak(Mortal enemy){
        if(!gameObject.isDead()) {
            enemy.takeDamage(damage);

            health -= 1;
            if (health <= 0) {
                death();
            } else {
                nextSmak = attackSpeed;
            }
        }
    }
    public void death(){
        if(!gameObject.isDead()) {
            gameObject.die();
        }
    }
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        if(nextSmak<0) {
            Mortal enemy = obj.getComponent(Mortal.class);
            if (enemy != null) {
                smak(enemy);
            }
        }
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
            if (guided && moveCommand() < speed * dt * 3) {
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

    public double moveCommand(){

        if(targeted()){
            movePos=target.position;
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
            self.transform.rotation= (float) ((atan(spdy/spdx))/(2*Math.PI)*360);
            if(first){

                self.transform.setFlippedX(spdx<0);
                self.transform.setFlippedY(spdy<0);

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


    public void moveCommand(List<Float> pos, GameObject self){

        this.target=null;
        this.movePos=new Vector2f(pos.get(0), pos.get(1));
        SharedMoveCommand();
    }
    public void moveCommand(Transform target){
        this.target=target;
        this.movePos=target.position;
        SharedMoveCommand();
    }
    public void SharedMoveCommand(){
        Rigidbody2D body=gameObject.getComponent(Rigidbody2D.class);
        body.setLinearDamping(body.moveDamping);
        super.gameObject.startMove(target);
        moveCommand();
    }
    @Override
    public void destroy(){
        this.target=null;
        this.rb=null;
    }

}
