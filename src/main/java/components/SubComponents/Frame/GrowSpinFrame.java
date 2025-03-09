package components.SubComponents.Frame;

import components.unitcapabilities.defaults.Sprite;
import jade.Transform;

public class GrowSpinFrame extends Frame {
    public float growX=1;
    public float growY=1;
    public float rotation=0;
    public GrowSpinFrame(GrowSpinFrame f) {
        try {
            sprite = f.sprite.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        frameTime=f.frameTime;
        growX=f.growX;
        growY=f.growY;
        rotation=f.rotation;
    }

    public GrowSpinFrame() {

    }

    public GrowSpinFrame(Sprite sprite, float frameTime) {

        this.sprite = sprite;
        this.frameTime = frameTime;
    }
    public GrowSpinFrame(Sprite sprite, float frameTime, float grow, float rotation) {
        //grow is how many times bigger or smaller it's supposed to be at the end of the frame ie: grow 10 means 10 times bigger, 1/10 10 times smaller etc
        //warning: animations should not change physical properties of the object but it's possible grow does in some way though it shouldn't so it might cause a bug if used on a physics object
        //warning: rotation changes physical properties of an object, currently both the sprite and any physical suff so ONLY use on non interactive only image things or possibly circecolliders

        this.sprite = sprite;

        this.frameTime = frameTime;
        this.growX=grow;
        this.growY=grow;
        this.rotation=rotation;//in degrees

    }
    @Override
    public boolean updateDraw(Transform trans,float dt) {
        if (rotation != 0) {
            trans.drawRotation += (dt) / frameTime * rotation;
        }
        if ((growX != 1)||(growY != 1)) {
            trans.scale.x*=1 /(1 + (((time) / frameTime) * (growX-1)));//undo the last one
            trans.scale.y*=1 /(1 + (((time) / frameTime) * (growY-1)));//undo the last one

            trans.scale.x*=1 + (((time+dt) / frameTime) * (growX-1));//perform the now one
            trans.scale.y*=1 + (((time+dt) / frameTime) * (growY-1));//perform the now one
        }
        return super.updateDraw(trans,dt);
    }
    @Override
    public void reset(Transform trans,boolean undo){
        if(undo) {
            if (rotation != 0) {
                trans.rotation -= (time) / frameTime * rotation;
            }
            if ((growX != 1) || (growY != 1)) {

                trans.scale.x *= 1 / (1 + (((time) / frameTime) * growX));//undo the last one
                trans.scale.y *= 1 / (1 + (((time) / frameTime) * growY));//undo the last one
            }
        }
        super.reset(trans,undo);
    }
}
