package components.unitcapabilities;

import components.unitcapabilities.defaults.Sprite;

public class Frame {
    public Sprite sprite;
    public float frameTime;
    public float grow=1;
    public float rotation=0;
    public Frame(Frame f) {
        try {
            sprite = f.sprite.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        frameTime=f.frameTime;
        grow=f.grow;
        rotation=f.rotation;
    }

    public Frame() {

    }

    public Frame(Sprite sprite, float frameTime) {

        this.sprite = sprite;
        this.frameTime = frameTime;
    }
    public Frame(Sprite sprite, float frameTime,float grow,float rotation) {
        //grow is how many times bigger or smaller it's supposed to be at the end of the frame ie: grow 10 means 10 times bigger, 1/10 10 times smaller etc
        //warning: animations should not change physical properties of the object but it's possible grow does in some way though it shouldn't so it might cause a bug if used on a physics object
        //warning: rotation changes physical properties of an object, currently both the sprite and any physical suff so ONLY use on non interactive only image things or possibly circecolliders

        this.sprite = sprite;
        this.frameTime = frameTime;
        this.grow=grow;
        this.rotation=rotation;//in degrees
    }
}
