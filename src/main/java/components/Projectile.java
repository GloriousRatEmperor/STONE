package components;

import jade.GameObject;
import jade.Window;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;
import util.AssetPool;
import util.JMath;

public class Projectile extends Component {
    public float health=1;
    public float damage=100;
    public float attackSpeed=10;
    private transient Rigidbody2D rb;
    public boolean isDead=false;
    public float nextSmak=0;
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
    }

    @Override
    public Projectile Clone(){
        return new Projectile();
    }
    public void smak(Mortal enemy){
        enemy.takeDamage(damage);

        health-=1;
        if(health<0){
            die();
        }else{
            rb.setIsSensor();
            nextSmak=attackSpeed;
        }
    }
    public void die(){
        gameObject.destroy();
    }
    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if(nextSmak<0) {
            Mortal enemy = obj.getComponent(Mortal.class);
            if (enemy != null) {
                smak(enemy);
            }
        }
    }
    @Override
    public void update(float dt) {
        if(nextSmak>=0) {
            nextSmak -= dt;
            if (nextSmak >= 0) {
                rb.setNotSensor();
            }

        }
    }


}
