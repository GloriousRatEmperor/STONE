package components;

import util.UniTime;

public class Modifier {
    public float mult;
    public String type;
    public float duration= Float.MAX_VALUE;
    public Modifier(float mult,String type,float duration){
        this.type=type;
        this.mult=mult;
        this.duration= UniTime.getFrame()+duration;
    }
}
