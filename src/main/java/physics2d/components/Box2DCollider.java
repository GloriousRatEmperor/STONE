package physics2d.components;

import jade.Window;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class Box2DCollider extends Collider {
    public Vector2f origin = new Vector2f();
    private Vector2f halfSize = new Vector2f(1);

    public Vector2f getOrigin() {
        return this.origin;
    }
    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(float X,float Y) {
        this.halfSize.set(X,Y);
    }
    @Override
    public void editorUpdateDraw(){
            Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
            DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
    }

    @Override
    public void reset(){
        Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
        if (rb != null) {
            Window.getPhysics().resetBox2DCollider(rb, this);
        }

    }

}
