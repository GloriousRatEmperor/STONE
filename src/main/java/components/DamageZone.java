package components;

import components.unitcapabilities.BoxSensor;
import components.unitcapabilities.damage.Damage;
import components.unitcapabilities.damage.Mortal;
import jade.GameObject;

public class DamageZone extends BoxSensor {
    public boolean recentDamage=false;
    public double damageFrequency=1;
    public double currentCooldown=0;

    private Damage damage;
    public DamageZone() {
        damage=new Damage(1);
    }
    public DamageZone(float sizex,float sizey){
        super(sizex,sizey);
        damage=new Damage(1);


    }
    public void setDamage(float dmg){
        damage.setBaseDamage(dmg);
    }
    @Override
    public void update(float dt){
        if(recentDamage){
            recentDamage=false;
            setActive(false);
            currentCooldown=damageFrequency;
        }
        currentCooldown-=dt;
        if(currentCooldown<0){
            setActive(true);
        }
        super.update(dt);


    }
    @Override
    public void objectDetected(GameObject collidingObject){
        Mortal mortal= collidingObject.getComponent(Mortal.class);
        if(mortal!=null){
            mortal.takeDamage(damage);
            recentDamage=true;
        }
    }


}
