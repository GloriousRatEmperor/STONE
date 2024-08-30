package SubComponents.Effects;

import SubComponents.SubComponent;
import jade.GameObject;

import java.util.List;

public class Effect implements SubComponent {
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

    @Override
    public void destroy() {
        
    }

    @Override
    public String Imgui(int AbilitySize, List<GameObject> activeGameObjects, int ID) {
        return null;
    }

    @Override
    public void EditorImgui() {

    }
}
