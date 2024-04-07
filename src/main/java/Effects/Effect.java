package Effects;

import jade.GameObject;

public class Effect {
    public float durationTotal;
    public float durationNow;
    public float power;

    public Effect(float duration, float power){
        this.durationTotal = duration;
        this.durationNow = duration;
        this.power = power;
    }
    public void apply(GameObject self){

    }
    public void expire(GameObject self){

    }
    public void update(float dt){
        durationNow -=dt;
    }

}
