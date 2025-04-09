package components.SubComponents.Animation;

import components.SubComponents.Animation.FrameEffects.GrowSpinFrameEffect;
import components.SubComponents.SubComponent;
import components.unitcapabilities.defaults.Sprite;
import jade.Transform;
import util.AssetPool;
import util.UniTime;

import java.util.ArrayList;
import java.util.List;

public class Animation extends SubComponent {

    public String title;
    public transient List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private transient int currentIndex = 0;
    private boolean started=false;
    private boolean doesLoop = false;
    private boolean dies = false;
    private float lastTime= UniTime.getExact();
    public Animation(Animation a){
        subComponents = animationFrames;
        title=a.title;
        animationFrames=a.animationFrames;
        currentIndex =a.currentIndex;
        doesLoop=a.doesLoop;
        dies=a.dies;
        lastTime=a.lastTime;
        for (Frame f:a.animationFrames){
            animationFrames.add(new Frame(f));
        }
    }
    public Animation(){
        subComponents = animationFrames;
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
        animationFrames.add(new Frame(sprite, frameTime));
    }
    public void addFrame(Sprite sprite, float frameTime,float size,float rotation) {
        Frame frame=new Frame(sprite,frameTime);
        if(size!=1||rotation!=0){
            frame.addFrameEffect(new GrowSpinFrameEffect(size,rotation));
        }

        animationFrames.add(frame);
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
        if(!started){
            started=true;
            lastTime=UniTime.getExact();
        }
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


    public void addSubComponent(SubComponent c){
        addFrame((Frame)c);
    }



}
