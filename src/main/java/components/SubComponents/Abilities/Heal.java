package components.SubComponents.Abilities;

import components.unitcapabilities.damage.Mortal;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;

public class Heal extends Ability{
    private float power=0.5f;
    @Override
    public Heal Copy(){
        Heal heal=new Heal(type);
        return heal;
    }
    public Heal(AbilityName type) {
        super(type);
        mp=50;
        sprite = Img.get("heal");


    }
    @Override
    public void updateDesc(){
        setDesc("restores "+ Math.round(power*100)+"%% missing hp");
    }
    @Override
    public void cast(final Vector2f pos, GameObject self) {
        Mortal mortal=self.getComponent(Mortal.class);
        if(mortal!=null){
            mortal.health+= power*(mortal.maxHealth-mortal.health);
        }
}
}
