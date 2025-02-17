package components.unitcapabilities.damage;

import util.UniTime;

public class Modifier {
    public float mult;
    public String type;
    public float duration;
    public float tempMult;
    public Modifier(float mult,String type,float duration){
        this.type=type;
        this.mult=mult;
        tempMult=mult;
        this.duration= UniTime.getFrame()+duration;
    }
    public void updateTemp(){
        tempMult=mult;
    }
}
