package components.SubComponents.Animation;

import components.SubComponents.Animation.FrameEffects.FrameEffect;
import components.SubComponents.SubComponent;
import components.unitcapabilities.defaults.Sprite;
import jade.Transform;

import java.util.ArrayList;
import java.util.List;

public class Frame extends SubComponent {
    public Sprite sprite;
    public float frameTime;
    public float time=0;
    private transient ArrayList<FrameEffect> frameEffects=new ArrayList<FrameEffect>();
    public Frame(Frame f) {
        subComponents = frameEffects;
        try {
            sprite = f.sprite.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        frameTime=f.frameTime;
    }

    public Frame() {
        subComponents = frameEffects;
    }

    public Frame(Sprite sprite, float frameTime) {

        this.sprite = sprite;
        this.frameTime = frameTime;
    }

    public boolean updateDraw(Transform trans,float dt) {
        for(FrameEffect effect:frameEffects){
            effect.updateDraw(this,trans,dt);
        }
        time+=dt;
        if(time>=frameTime){
            return true;
        }else{
            return false;
        }
    }
    public void reset(Transform trans,boolean undo){
        for(FrameEffect effect:frameEffects){
            effect.reset(this,trans,undo);
        }
        time=0;
    }
    @Override
    public List<? extends SubComponent> getSubComponents(){

        return frameEffects;
    }

    public void addSubComponent(SubComponent c){
        addFrameEffect((FrameEffect) c);
    }
    public void addFrameEffect(FrameEffect effect){
        frameEffects.add(effect);
    }
    public ArrayList<FrameEffect> getFrameEffects(){
        return frameEffects;
    }
    public void removeFrameEffect(FrameEffect effect){
        frameEffects.remove(effect);
    }

}
