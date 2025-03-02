package components.unitcapabilities.damage;

import components.Component;
import components.unitcapabilities.Brain;
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
    public float magicArmor=0;
    public boolean guardMode=true;
    public float chargeDefense=0; //charge defense is damage reduction from charging enemies that makes the bonus x times smaller
    private transient Rigidbody2D rb;
    @Override
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

        //ORDER: 1+: resistMod (will affect mods tempmult it theoretically works if this happens like 20s before the actual hit but will only work on next hit)
              // resistMod=makes the bonus(or benefit) resistance times smaller, resistance 0.5 also makes it twice as big

              // 2+: calcMod() -> calcs tempMod from tempmults of the modifiers

              // 3+: resistType (will reduce the damage of anything with a type, for example all fire damage but affects tempMod so it HAS TO BE called AFTER calcMod or it has no effect)
              // resistType=makes damage resistance times smaller (assuming it has the type), resistance 0.5 also makes it twice as big

              //+ 4: calcDamage() -> will calculate all ap and non ap damage but keep them separate

              // 5: resistPhysical or resistMagic -> armor calls resistPhysical, magicArmor will do the same but for magic
                    //damage=dmg.allDmg/(1+(armor/dmg.allDmg)) is the formula

              // 6: mergeDamage() -> will merge physical and magical damage

              // 7: resistAllPercent(), resistAllFlat() -> resist all the damage in some way, but no option to resist one part of the damage anymore

              // 8:takedamage(...)

              // FINALLY: updateMod() updateDamage() -> sets the temps back to permanent values




        if(this.isAlive) {
            if(chargeDefense!=0) {
                dmg.resistMod("charge", chargeDefense);
            }
            dmg.calcMod();



            dmg.calcDamage();
            dmg.resistPhysical(armor);
            dmg.resistMagic(magicArmor);
            dmg.mergeDamage();

            health-=dmg.getDamage();
            if (health <= 0) {
                death();
            }else if(!guardMode&&dmg.owner!=-1){
                Brain brain=gameObject.getComponent(Brain.class);
                brain.considerAggro(dmg.owner);
            }
        }
    }
    @Override
    public void update(float dt){
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
