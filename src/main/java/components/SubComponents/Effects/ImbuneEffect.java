package components.SubComponents.Effects;

import jade.GameObject;

public abstract class ImbuneEffect extends Effect{
    public ImbuneEffect(float duration, float power) {
        super(duration, power);
        type="Imbune";

    }
    @Override
    public void update(float dt){

    }
    public Effect imbune(GameObject parent){
        return null;
    }
}
