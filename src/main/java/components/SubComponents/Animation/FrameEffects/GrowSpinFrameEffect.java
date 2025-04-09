package components.SubComponents.Animation.FrameEffects;

import components.SubComponents.Animation.Frame;
import jade.Transform;

public class GrowSpinFrameEffect extends FrameEffect {
    public float growX=1;
    public float growY=1;
    public float rotation=0;

    public GrowSpinFrameEffect() {

    }

    public GrowSpinFrameEffect(float grow, float rotation) {
        //grow is how many times bigger or smaller it's supposed to be at the end of the frame ie: grow 10 means 10 times bigger, 1/10 10 times smaller etc

        //warning: animations should not change physical properties of the object but it's possible grow does in some way though it shouldn't so it might cause a bug if used on a physics object

        this.growX=grow;
        this.growY=grow;
        this.rotation=rotation;//in degrees

    }
    @Override
    public void updateDraw(Frame self, Transform trans,float dt) {
        if (rotation != 0) {
            trans.drawRotation += (dt) / self.frameTime * rotation;
        }
        if ((growX != 1)||(growY != 1)) {
            trans.scale.x*=1 /(1 + (((self.time) / self.frameTime) * (growX-1)));//undo the last one
            trans.scale.y*=1 /(1 + (((self.time) / self.frameTime) * (growY-1)));//undo the last one

            trans.scale.x*=1 + (((self.time+dt) / self.frameTime) * (growX-1));//perform the now one
            trans.scale.y*=1 + (((self.time+dt) / self.frameTime) * (growY-1));//perform the now one
        }
    }
    @Override
    public void reset(Frame self,Transform trans,boolean undo){
        if(undo) {
            if (rotation != 0) {
                trans.drawRotation -= (self.time) / self.frameTime * rotation;
            }
            if ((growX != 1) || (growY != 1)) {

                trans.scale.x *= 1 / (1 + (((self.time) / self.frameTime) * growX));//undo the last one
                trans.scale.y *= 1 / (1 + (((self.time) / self.frameTime) * growY));//undo the last one
            }
        }
    }
}
