package components.SubComponents.Effects;

import util.Img;

public class AlternatorMarked extends Effect {


    public AlternatorMarked(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("alteratormark");
        type="curse";
        updateDesc();
    }


    @Override
    public void updateDesc(){
        setDesc("enables alternator spells on this unit (with "+Math.round(power*100)+"%% efficiency)");
    }


}
