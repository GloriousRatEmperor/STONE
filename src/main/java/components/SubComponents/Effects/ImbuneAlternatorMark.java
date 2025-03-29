package components.SubComponents.Effects;

import jade.GameObject;
import util.Img;

public class ImbuneAlternatorMark extends ImbuneEffect{
    public float markPower=1f;
    public ImbuneAlternatorMark(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("alteratormark");
        updateDesc();
    }


    @Override
    public void updateDesc(){
        setDesc("causes hit units be marked for "+Math.round(power*10)/10f+" seconds (with "+Math.round(markPower*100)+"%% efficiency)");
    }

    @Override
    public Effect imbune(GameObject parent){
        AlternatorMarked marked=new AlternatorMarked(power,markPower);
        return marked;
    }

}
