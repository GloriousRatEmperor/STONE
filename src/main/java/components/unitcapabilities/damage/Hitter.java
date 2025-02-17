package components.unitcapabilities.damage;

import components.Component;
import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;

public class Hitter extends Component {
    public float dmg=2;
    public int alliedH =0;
    public float attackSpeed=0.1f;
    public float nextSmak=0;
    public float chargeBonus=0; //charge bonus 1 means +100% damage for max speed,
                                // the damage is sqrt with the speed until 1.5 times max speed (achievable thu like units pushing and stuff probably) where it's maxed at 150%.
    private Damage damage;
    public void smak(Mortal enemy){
        MoveContollable move=gameObject.getComponent(MoveContollable.class);
        float charge=0;
        if(move!=null){
            Rigidbody2D body=gameObject.getComponent(Rigidbody2D.class);
            charge=(float)Math.min(Math.pow( body.getSpeed()/ move.speed,2),1.5)*chargeBonus;

        }
        if(charge!=0) {
            damage.addGlobalMult(charge+1, "charge");
        }

        enemy.takeDamage(damage);

    }
    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {

        if(nextSmak<0) {
            Mortal enemy = obj.getComponent(Mortal.class);
            if (enemy != null) {
                if (enemy.alliedM != this.alliedH) {
                    smak(enemy);
                    nextSmak = attackSpeed;
                }
            }
        }
    }
    public Hitter(){

    }
    public Hitter(float attack, float attackSpeed){
        dmg=attack;
        this.attackSpeed=attackSpeed;
    }
    public void start(){
        damage=new Damage(dmg,"physical");
        damage.owner=this.gameObject.getUid();
    }
    @Override
    public void update(float dt) {
        nextSmak-=dt;
    }
    @Override
    public void begin(){
        if(this.alliedH==0){
            this.alliedH=super.gameObject.allied;
        }
    }
}
