package components;

import jade.Transform;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2d.components.Rigidbody2D;

import static renderer.DebugDraw.addLine2D;

public class Mortal extends Component {
    public float health=30;
    public float maxHealth=30;
    public boolean isAlive=true;
    public int alliedM =0;
    public float armor=0;
    public float chargeDefense; //charge defense is damage reduction from charging enemies that works the same as armor vs attack
    private transient Rigidbody2D rb;
    @Override
    public Mortal Clone(){
        return new Mortal();
    }
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);

    }
    public void takeDamage(float damage){
        if(this.isAlive) {
            damage=damage/(1+(armor/damage));
            health -= damage;
            if (health <= 0) {
                death();
            }
        }
    }
    public void takeDamage(Damage dmg){
        if(this.isAlive) {
            dmg.allDamage(); // calculates the damage and updates alldmg
            float damage=dmg.allDmg/(1+(armor/dmg.allDmg));
            health -= damage;
            if (health <= 0) {
                death();
            }
        }
    }
    @Override
    public void die(){
        isAlive=false;
    }
    @Override
    public void destroy(){
        isAlive=false;
    }

    public void death(){
        isAlive=false;
        gameObject.die();
    }
    @Override
    public void begin(){
        if(this.alliedM==0){
            this.alliedM=super.gameObject.allied;
        }
    }
    @Override
    public void updateDraw() {
        Transform tra=gameObject.transform;
        if (Window.get().allied == this.alliedM) {

            addLine2D(new Vector2f( tra.drawPos.x-tra.scale.x/2,tra.drawPos.y+tra.scale.y/2+0.1f), new Vector2f( tra.drawPos.x+(tra.scale.x/2)*(health/maxHealth*2-1),tra.drawPos.y+tra.scale.y/2+0.1f), new Vector3f(0,1,0), 1);
        }else{
            addLine2D(new Vector2f( tra.drawPos.x-tra.scale.x/2,tra.drawPos.y+tra.scale.y/2+0.1f), new Vector2f( tra.drawPos.x+(tra.scale.x/2)*(health/maxHealth*2-1),tra.drawPos.y+tra.scale.y/2+0.1f), new Vector3f(1,0,0), 1);
        }
    }

    public Mortal(float health,float armor){
        this.health = health;
        maxHealth = health;
        this.armor=armor;
    }
    public Mortal(){

    }

}
