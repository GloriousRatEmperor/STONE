package physics2d.components;

import jade.Window;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class CircleCollider extends Collider {

    public float radius = 1f;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        resetFixtureNextFrame = true;
        this.radius = radius;
    }

    @Override
    public void editorUpdateDraw() {

        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addCircle(center, this.radius);
        if (resetFixtureNextFrame) {
            resetFixture();

        }
    }
    public void reset(){
        Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
        if (rb != null) {
            Window.getPhysics().resetCircleCollider(rb, this);
        }

    }
}
