package components;

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
                                // the damage is linear with the speed until 1.5 times max speed (achievable thu like units pushing and stuff probably) where it's maxed at 150%.

    public void smak(Mortal enemy){
        MoveContollable move=gameObject.getComponent(MoveContollable.class);
        float chargedmg=0;
        if(move!=null){
            Rigidbody2D body=gameObject.getComponent(Rigidbody2D.class);
            chargedmg=(float)Math.min(Math.pow( body.getSpeed()/ move.speed,2),1.5)*chargeBonus*dmg;

        }
        enemy.takeDamage(dmg+ chargedmg);

    }
    @Override
    public Hitter Clone(){
        return new Hitter();
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
    @Override
    public void update(float dt) {
        nextSmak-=dt;
    }
    @Override
    public void begin(){
        this.alliedH = super.gameObject.allied;
    }
}
