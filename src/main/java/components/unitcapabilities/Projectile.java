package components.unitcapabilities;

import components.Component;
import components.SubComponents.Effects.Effect;
import components.SubComponents.Effects.ImbuneEffect;
import components.unitcapabilities.damage.Damage;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.Effects;
import jade.GameObject;
import jade.Transform;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;

import java.util.List;

import static java.lang.Math.*;

public class Projectile extends Component {
    public float health=1;
    public Damage damage;
    public Boolean guided=false;
    public float attackSpeed=10;
    public float tolerance;
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
    public Projectile(float damage){
        this.damage=new Damage(damage);
    }
    public Projectile(float damage,float magicPercent){
        this.damage=new Damage(damage,magicPercent);
    }
    public void smak(Mortal enemy){
        if(!gameObject.isDead()) {
            Effects thisEffects=gameObject.getComponent(Effects.class);
            Effects theirEffects=enemy.gameObject.getComponent(Effects.class);
            if(theirEffects!=null&&thisEffects!=null){
                for(Effect e: thisEffects.getEffects("Imbune")){
                    theirEffects.addEffect(((ImbuneEffect)e).imbune(gameObject));
                }
            }
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
            gameObject.die(gameObject);
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
            if (guided && moveCommand() < tolerance) {
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
