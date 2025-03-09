package components.SubComponents.Frame;

import components.SubComponents.SubComponent;
import components.unitcapabilities.defaults.Sprite;
import jade.Transform;

public class Frame extends SubComponent {
    public Sprite sprite;
    public float frameTime;
    public float time=0;
    public Frame(Frame f) {
        try {
            sprite = f.sprite.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        frameTime=f.frameTime;
    }

    public Frame() {

    }

    public Frame(Sprite sprite, float frameTime) {

        this.sprite = sprite;
        this.frameTime = frameTime;
    }

    public boolean updateDraw(Transform trans,float dt) {
        time+=dt;
        if(time>=frameTime){

            return true;
        }else{
            return false;
        }
    }
    public void reset(Transform trans,boolean undo){
        time=0;
    }

}
