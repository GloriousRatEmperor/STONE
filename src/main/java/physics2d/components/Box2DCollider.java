package physics2d.components;

import components.Component;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class Box2DCollider extends Component {
    public Box2DCollider Clone(){
        return new Box2DCollider();
    }
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();
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

    @Override
    public void editorUpdateDraw(){
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
    }
}
