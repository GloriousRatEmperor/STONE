package components.SubComponents.Frame.FrameEffects;


import components.SubComponents.Frame.Frame;
import jade.Transform;

public class VisualMoveFrameEffect extends FrameEffect {
    public float moveX=0;
    public float moveY=0;

    public VisualMoveFrameEffect() {

    }

    public VisualMoveFrameEffect(float moveX, float moveY) {
        this.moveX=moveX;
        this.moveY=moveY;

    }
    @Override
    public void updateDraw(Frame self, Transform trans,float dt) {
        trans.drawOffset.add(moveX*(dt) / self.frameTime,moveY*(dt) / self.frameTime);
    }
    @Override
    public void reset(Frame self,Transform trans,boolean undo){
        if(undo) {
            trans.drawOffset.sub(moveX*(self.time) / self.frameTime,moveY*(self.time) / self.frameTime);
        }
    }
}
