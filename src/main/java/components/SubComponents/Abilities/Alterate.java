package components.SubComponents.Abilities;

import enums.AbilityName;
import util.Img;

public class Alterate extends Ability {


    public Alterate(AbilityName type) {
        super(type);
        sprite = Img.get("alteratormark");
        updateDesc();
    }
    @Override
    public void updateDesc(){
        setDesc("does... nothing?");
    }
}
