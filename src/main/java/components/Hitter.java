package components;

import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Hitter extends Component {
    public float dmg=1;
    public int allied =1;
    public float attackSpeed=0.1f;
    public float nextSmak=0;
    public void smak(Mortal enemy){
        enemy.takeDamage(dmg);
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
                if (enemy.allied != this.allied) {
                    smak(enemy);
                    nextSmak = attackSpeed;
                }
            }
        }
    }
    @Override
    public void update(float dt) {
        nextSmak-=dt;
    }
}
