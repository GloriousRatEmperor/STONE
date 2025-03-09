package components.unitcapabilities;

import components.SubComponents.Frame.Frame;
import components.SubComponents.Frame.GrowSpinFrame;
import components.unitcapabilities.defaults.Sprite;
import jade.Transform;
import util.AssetPool;
import util.UniTime;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private transient int currentIndex = 0;
    private boolean doesLoop = false;
    private boolean dies = false;
    private float lastTime= UniTime.getExact();
    public Animation(Animation a){
        title=a.title;
        animationFrames=a.animationFrames;
        currentIndex =a.currentIndex;
        doesLoop=a.doesLoop;
        dies=a.dies;
        lastTime=a.lastTime;
        System.out.println("aaa it is used");
        for (Frame f:a.animationFrames){
            animationFrames.add(new Frame(f));
        }
    }
    public Animation(){

    }

    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }
    public void addFrame(Frame frame) {
        animationFrames.add(frame);

    }
    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new GrowSpinFrame(sprite, frameTime));
    }
    public void addFrame(Sprite sprite, float frameTime,float size,float rotation) {
        animationFrames.add(new GrowSpinFrame(sprite, frameTime,size,rotation));
    }


    public void addFrames(List<Sprite> sprites, float frameTime) {
        for (Sprite sprite : sprites) {
            this.animationFrames.add(new GrowSpinFrame(sprite, frameTime));
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
        Frame currentFrame = animationFrames.get(currentIndex);
        if(currentFrame.updateDraw(trans,currentTime - lastTime)){
            currentIndex++;
        }
        lastTime=currentTime;
        if(animationFrames.size()==currentIndex){
            if(doesLoop){
                reset(trans,false);
            }else if(dies) {
                trans.gameObject.destroy();
                return true;
            }else{
                return true;
            }
        }
        return false;
    }

    public Sprite getCurrentSprite() {
        if (currentIndex < animationFrames.size()) {
            return animationFrames.get(currentIndex).sprite;
        }

        return defaultSprite;
    }
    public Sprite getFirstSprite(){
        return animationFrames.get(0).sprite;
    }
    public void reset(Transform trans,boolean undo){
        currentIndex =0;
        for(Frame frame:animationFrames){
            frame.reset(trans,undo);
        }
    }
}
