package components;

import SubComponents.Effects.ImbuneEffect;
import SubComponents.SubComponent;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import util.Unit;

import java.util.ArrayList;
import java.util.List;

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
    public transient List<ImbuneEffect> projectileEffects = new ArrayList<>();
    private CircleCollider rangeHitbox;
    public Shooter(float range,float attackSpeed, String projectileName){
        this.attackSpeed=attackSpeed;
        this.range=range;
        this.projectileName=projectileName;
        this.subComponents=this.projectileEffects;
    }
    public Shooter(){
        this.subComponents=this.projectileEffects;
    }
    public void addEffect(ImbuneEffect effect){
        effect.owner=this;
        projectileEffects.add(effect);
    }
    @Override
    public void addSubComponent(SubComponent c) {
        ImbuneEffect a=((ImbuneEffect)c);
        addEffect(a);
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
            Effects peffs =projectile.getComponent(Effects.class);
            for(ImbuneEffect e: projectileEffects){
                peffs.addEffect( (e).imbune());
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
            if(collidingObject.getComponent(Mortal.class)!=null) {
                shoot(collidingObject);
            }
        }
    }
    @Override
    public void start(){
        super.start();
        rangeHitbox=new ShootCollider();
        rangeHitbox.setRadius(range);
        rangeHitbox.collisionGroup=-2;
        gameObject.addComponent(rangeHitbox);


    }
    @Override

    public void destroy() {
        this.projectileEffects.clear();
    }
}
