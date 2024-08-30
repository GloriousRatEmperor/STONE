package physics2d.components;

import components.Component;
import jade.Window;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class CircleCollider extends Component {
    @Override
    public CircleCollider Clone(){
        return new CircleCollider();
    }
    public float radius = 1f;
    public int collisionGroup=1;
    public int allyOverride=0;
    public boolean isSensor=false;
    public Boolean enabled=true;
    protected transient boolean resetFixtureNextFrame = false;
    public Vector2f offset = new Vector2f();

    public float getRadius() {
        return radius;
    }

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f newOffset) { this.offset.set(newOffset); }

    public void setRadius(float radius) {
        resetFixtureNextFrame = true;
        this.radius = radius;
    }
    public int getAllied(){
        if(allyOverride!=0){
            return allyOverride;
        }
        return gameObject.allied;
    }

    @Override
    public void editorUpdateDraw() {

            Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
            DebugDraw.addCircle(center, this.radius);
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }
    public void setDisabled() {
        if (enabled) {
            this.enabled = false;

            gameObject.getComponent(Rigidbody2D.class).setDisabled(this);
        }
    }
    public void setEnabled(){
        if(!enabled) {
            this.enabled = true;
            resetFixture();//maybe try resetFixtureNextFrame=true if there problems?

        }
    }
    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
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
                Window.getPhysics().resetCircleCollider(rb, this);
            }
        }
    }

}
