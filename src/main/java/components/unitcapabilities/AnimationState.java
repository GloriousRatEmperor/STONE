package components.unitcapabilities;

import components.unitcapabilities.defaults.Sprite;
import jade.Transform;
import util.AssetPool;
import util.UniTime;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {

    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private float time = 0.0f;
    private transient int currentSprite = -1;
    private boolean doesLoop = false;
    private boolean dies = false;
    private float lastTime= UniTime.getExact();
    public AnimationState(AnimationState a){
        title=a.title;
        animationFrames=a.animationFrames;
        time=a.time;
        currentSprite=a.currentSprite;
        doesLoop=a.doesLoop;
        dies=a.dies;
        lastTime=a.lastTime;
        for (Frame f:a.animationFrames){
            animationFrames.add(new Frame(f));
        }
    }
    public AnimationState(){

    }

    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }
    public void addFrame(Sprite sprite, float frameTime,float size,float rotation) {
        animationFrames.add(new Frame(sprite, frameTime,size,rotation));
    }


    public void addFrames(List<Sprite> sprites, float frameTime) {
        for (Sprite sprite : sprites) {
            this.animationFrames.add(new Frame(sprite, frameTime));
        }
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }
    public void setDeathOnCompletion(boolean dies) {
        this.dies = dies;
    }

    public boolean updateDraw(Transform trans) {
        float currentTime=UniTime.getExact();
        if(lastTime<currentTime) {
            if (currentSprite < animationFrames.size()) {
                if (time <= currentTime) {
                    if(time!=0.0f) {
                        Frame currentFrame = animationFrames.get(currentSprite);

                        if (currentFrame != null) {
                            if (currentFrame.rotation != 0) {
                                trans.rotation += (currentTime - lastTime) / currentFrame.frameTime * currentFrame.rotation;
                            }
                            if (currentFrame.grow != 1) {
                                trans.scale.mul(1 / ((1 + (-time + lastTime) / currentFrame.frameTime) * currentFrame.grow + (time - lastTime) / currentFrame.frameTime));//undo the last one

                                trans.scale.mul(currentFrame.grow);//perform the now one
                            }
                        }
                    }

                    if (!(currentSprite == animationFrames.size() - 1 && !doesLoop)) {
                        currentSprite = (currentSprite + 1) % animationFrames.size();
                    } else if (currentSprite == animationFrames.size() - 1) {
                        if(dies){
                            trans.gameObject.destroy();
                            return true;
                        }else{
                            return true;
                        }

                    }
                    time = currentTime + animationFrames.get(currentSprite).frameTime;
                }else if(time!=0.0f) {
                    Frame currentFrame = animationFrames.get(currentSprite);
                    if (currentFrame.rotation != 0) {
                        trans.rotation += (currentTime - lastTime) / currentFrame.frameTime * currentFrame.rotation;
                    }
                    if (currentFrame.grow != 1) {
                        trans.scale.mul( 1/((1+(-time+lastTime)/currentFrame.frameTime) * currentFrame.grow+(time-lastTime)/currentFrame.frameTime));//undo the last one

                        trans.scale.mul( (1+(-time+currentTime)/currentFrame.frameTime) * currentFrame.grow+(time-currentTime)/currentFrame.frameTime);//perform the now one
                    }

                }
                lastTime = currentTime;
            }
        }
        return false;
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }

        return defaultSprite;
    }
    public Sprite getFirstSprite(){
        return animationFrames.get(0).sprite;
    }
    public void reset(){
        currentSprite=-1;
        time= UniTime.getExact();
    }
}
