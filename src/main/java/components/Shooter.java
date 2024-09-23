package components;

import SubComponents.Effects.Effect;
import SubComponents.Effects.ImbuneEffect;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import util.Unit;

import java.util.ArrayList;

import static jade.Window.getScene;

public class Shooter extends Component{
    @Override
    public Shooter Clone(){
        return new Shooter();
    }
    private float range=2;
    public float damageMult=1;
    public float speedMult=1;
    private float attackSpeed=1;
    private String projectileName="arrow";
    private float nextAttack=0;
    private CircleCollider rangeHitbox;
    public Shooter(float range,float attackSpeed, String projectileName){
        this.attackSpeed=attackSpeed;
        this.range=range;
        this.projectileName=projectileName;
    }
    public Shooter(){

    }

    @Override
    public void update(float dt){
        this.nextAttack-=dt;
        if(this.nextAttack<=0){
            rangeHitbox.setEnabled();
            gameObject.getComponent(Rigidbody2D.class).setSleepAllowed(false);
        }
    }
    public void shoot(GameObject go){
        if(nextAttack<=0) {
            GameObject projectile = Unit.makeProjectile(projectileName, this.gameObject.transform.position, go.transform, this.gameObject.allied);
            projectile.getComponent(Projectile.class).damage*=damageMult;
            projectile.getComponent(Projectile.class).speed*=speedMult;
            Effects effects =this.gameObject.getComponent(Effects.class);
            if(effects!=null){
                ArrayList<Effect> eff=effects.getEffects("ImbuneProjectiles");
                Effects peffs =projectile.getComponent(Effects.class);
                for(Effect e: eff){
                    peffs.addEffect( ((ImbuneEffect)e).imbune());
                }
            }

            getScene().addGameObjectToScene(projectile);
            nextAttack=attackSpeed;
            rangeHitbox.setDisabled();
            go.getComponent(Rigidbody2D.class).setSleepAllowed(true);

        }
    }
    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if(contact.m_fixtureB.getUserData().equals(rangeHitbox)||contact.m_fixtureA.getUserData().equals(rangeHitbox)) {
            shoot(collidingObject);
        }
    }
    @Override
    public void start(){
        rangeHitbox=new ShootCollider();
        rangeHitbox.setRadius(range);
        rangeHitbox.collisionGroup=-2;
        gameObject.addComponent(rangeHitbox);


    }
}
