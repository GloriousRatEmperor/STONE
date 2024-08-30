package components;

import SubComponents.Effects.Effect;
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
    private float damage=20;
    private float attackSpeed=1;
    private String projectileName="arrow";
    private Sprite img;
    private float nextAttack=0;
    private ArrayList< Effect> projectileEffects=new ArrayList<>();
    private CircleCollider rangeHitbox;
    public Shooter(float range,float attackSpeed, float damage,Sprite projectileImg){
        this.attackSpeed=attackSpeed;
        this.range=range;
        this.damage=damage;
        this.img=projectileImg;
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
    public void shoot(GameObject gameObject){

        if(nextAttack<=0) {
            GameObject projectile = Unit.makeProjectile(projectileName, this.gameObject.transform.position, gameObject.transform, this.gameObject.allied);
            getScene().addGameObjectToScene(projectile);
            nextAttack=attackSpeed;
            rangeHitbox.setDisabled();
            gameObject.getComponent(Rigidbody2D.class).setSleepAllowed(true);
        }
    }
    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if(contact.m_fixtureB.getUserData().equals(rangeHitbox)) {
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
