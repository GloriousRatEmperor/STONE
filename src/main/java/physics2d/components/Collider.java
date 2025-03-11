package physics2d.components;

import components.Component;
import jade.Window;
import org.joml.Vector2f;

public abstract class Collider extends Component {

    public int collisionGroup=1;
    public int allyOVerride=0;
    public Boolean isSensor=false;
    public Boolean resetFixtureNextFrame=true;
    public Boolean enabled=true;
    public Vector2f offset = new Vector2f();
    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(float X,float Y) {
        this.offset.set(X,Y);
    }


    public void setDisabled(){
        if (enabled) {
            this.enabled = false;
            resetFixtureNextFrame=true;
        }
    }
    public void setEnabled(){
        if(!enabled) {
            this.enabled = true;
            resetFixtureNextFrame=true;
        }

    }
    @Override
    public void editorUpdateDraw(){
    }
    public int getAllied(){
        if(allyOVerride==0) {
            return gameObject.allied;
        }else{
            return allyOVerride;
        }
    }
    @Override
    public void update(float dt){
        if(resetFixtureNextFrame){
            resetFixture();
        }

    }
    public int getCollisionGroup(){
        return collisionGroup;
    }
    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;
        if (gameObject != null) {
            if(enabled){
                reset();
            }else{
                disable();
            }

        }
    }
    public void reset(){
    }
    public void disable(){
        gameObject.getComponent(Rigidbody2D.class).setDisabled(this);
    }
}
