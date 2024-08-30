package physics2d.components;

import components.Component;
import jade.Window;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class Box2DCollider extends Component {
    @Override
    public Box2DCollider Clone(){
        return new Box2DCollider();
    }
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();
    public int collisionGroup=1;
    public int allyOVerride=0;
    public Boolean isSensor=false;
    public Boolean resetFixtureNextFrame=false;
    public Boolean enabled;
    private Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(float X,float Y) {
        this.offset.set(X,Y);
    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(float X,float Y) {
        this.halfSize.set(X,Y);
    }

    public Vector2f getOrigin() {
        return this.origin;
    }
    public void setDisabled(){
        enabled=false;
        gameObject.getComponent(Rigidbody2D.class).setDisabled(this);
    }
    public void setEnabled(){
        enabled=true;

    }
    @Override
    public void editorUpdateDraw(){
            Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
            DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
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
            Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
            if (rb != null) {
                Window.getPhysics().resetBox2DCollider(rb, this);
            }
        }
    }

}
