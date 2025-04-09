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
import physics2d.components.Rigidbody2D;

public abstract class Projectile extends Component {
    public float health=1;
    public Damage damage;
    public Boolean guided;
    public float attackSpeed=10;

    public float speed;
    public transient Rigidbody2D rb;
    public float nextSmak=0;

    public Boolean first=true;
    public float lifespan=20;
    public float acceleration=1;

    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
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
            gameObject.die();
        }
    }
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        if(nextSmak<0) {
            Mortal enemy = obj.getComponent(Mortal.class);
            if (enemy != null&&obj.allied!= gameObject.allied) {
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
        }else {
            death();
        }
    }

    public double moveCommand(){
        return -1f;
    }



    public void SharedMoveCommand(Transform target){
        Rigidbody2D body=gameObject.getComponent(Rigidbody2D.class);
        body.setLinearDamping(body.moveDamping);
        if(target!=null){
            super.gameObject.startMove(target);
        }
        moveCommand();
    }
    @Override
    public void destroy(){
        this.rb=null;
    }


}
