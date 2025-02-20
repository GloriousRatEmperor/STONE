package components.unitcapabilities;

import components.SubComponents.Effects.ImbuneEffect;
import components.SubComponents.SubComponent;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.Effects;
import jade.GameObject;
import physics2d.components.Rigidbody2D;
import util.Unit;

import java.util.ArrayList;
import java.util.List;

import static jade.Window.getScene;

public class Shooter extends CircleDetector {

    public float damageMult=1;
    public float speedMult=1;
    private float attackSpeed=1;
    private String projectileName="arrow";
    private float nextAttack=0;
    public transient List<ImbuneEffect> projectileEffects = new ArrayList<>();
    public Shooter(float range,float attackSpeed, String projectileName){
        super(range);
        this.attackSpeed=attackSpeed;
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
            detectCircle.setEnabled();
            detectCircle.update(dt);
            gameObject.getComponent(Rigidbody2D.class).setSleepAllowed(false);
        }
    }
    public void shoot(GameObject go){
        if(nextAttack<=0) {
            GameObject projectile = Unit.makeProjectile(projectileName,gameObject.getUid(), this.gameObject.transform.position, go.transform, this.gameObject.allied);
            projectile.getComponent(Projectile.class).damage.addGlobalMult(damageMult,"baseMult");
            projectile.getComponent(Projectile.class).speed*=speedMult;
            Effects peffs =projectile.getComponent(Effects.class);
            for(ImbuneEffect e: projectileEffects){
                peffs.addEffect( (e).imbune());
            }
            getScene().addGameObjectToScene(projectile);
            nextAttack=attackSpeed;
            detectCircle.setDisabled();
            go.getComponent(Rigidbody2D.class).setSleepAllowed(true);

        }
    }
    @Override
    public void objectDetected(GameObject collidingObject) {
        if(collidingObject.getComponent(Mortal.class)!=null) {
            shoot(collidingObject);
        }

    }
}
