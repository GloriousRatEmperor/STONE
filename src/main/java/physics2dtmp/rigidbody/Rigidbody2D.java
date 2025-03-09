package physics2dtmp.rigidbody;

import components.Component;
import jade.Transform;
import org.joml.Vector2f;
import physics2dtmp.primitives.Collider2D;

public class Rigidbody2D extends Component {
    // ???????????? there ARE TWO RIGIDBODIES WTH, I thinks this one be fake
    private Transform rawTransform;
    private Collider2D collider;

    private Vector2f position = new Vector2f();
    private Vector2f pastPos = new Vector2f();
    private Vector2f drawPos = new Vector2f();
    private float rotation = 0.0f;
    private boolean flippedX = false;
    private boolean flippedY = false;
    private float mass = 0.0f;
    private float inverseMass = 0.0f;

    private Vector2f forceAccum = new Vector2f();
    private Vector2f linearVelocity = new Vector2f();
    private float angularVelocity = 0.0f;
    private float linearDamping = 0.0f;
    private float angularDamping = 0.0f;

    // Coefficient of restitution
    private float cor = 1.0f;

    private boolean fixedRotation = false;

    public Rigidbody2D(){
        System.out.println("aaa");
    }
    public Vector2f getPosition() {
        return position;
    }
    public Vector2f getDrawPosition() {
        return drawPos;
    }
    public Vector2f getPastPosition() {
        return pastPos;
    }
    public void updatePastPos(){pastPos=new Vector2f(position);};
    public void updateDrawPos(float fraction){
        drawPos.x=pastPos.x* (1-fraction)+position.x * fraction;
        drawPos.y=pastPos.y* (1-fraction)+position.y * fraction;
    }

    public void physicsUpdate(float dt) { //never used
        System.out.println("physics update being used???");
        if (this.mass == 0.0f) return;

        // Calculate linear velocity
        Vector2f acceleration = new Vector2f(forceAccum).mul(this.inverseMass);
        linearVelocity.add(acceleration.mul(dt));
        this.position.add(new Vector2f(linearVelocity).mul(dt));
        synchCollisionTransforms();
        clearAccumulators();
    }

    @Override
    public void update(float dt){
        System.out.println();
    }
    public void synchCollisionTransforms() {
        System.out.println("aaa");
        if (rawTransform != null) {
            rawTransform.position.set(this.position);
        }
    }

    public void clearAccumulators() {
        this.forceAccum.zero();
    }

    public void setTransform(Vector2f position, float rotation) {
        this.position.set(position);
        this.rotation = rotation;
    }

    public void setTransform(Vector2f position) {
        this.position.set(position);
    }

    public void setVelocity(Vector2f velocity) {
        this.linearVelocity.set(velocity);
    }

    public Vector2f getVelocity() {
        return this.linearVelocity;
    }

    public float getRotation() {
        return rotation+100;
    }
    public boolean isflippedY() {
        return flippedY;
    }
    public void setFlippedY(boolean direction) {
         flippedY=direction;
    }
    public void flipY() {
        flippedY=!flippedY;
    }
    public boolean isflippedX() {
        return flippedX;
    }
    public void setFlippedX(boolean direction) {
        flippedX=direction;
    }
    public void flipX() {
        flippedX=!flippedX;
    }
    public float getMass() {
        return mass;
    }

    public float getInverseMass() {
        return this.inverseMass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        if (this.mass != 0.0f) {
            this.inverseMass = 1.0f / this.mass;
        }
    }

    public boolean hasInfiniteMass() {
        return this.mass == 0.0f;
    }

    public void addForce(Vector2f force) {
        this.forceAccum.add(force);
    }

    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position.set(rawTransform.position);
    }

    public void setCollider(Collider2D collider) {
        this.collider = collider;
    }

    public Collider2D getCollider() {
        return this.collider;
    }

    public float getCor() {
        return cor;
    }

    public void setCor(float cor) {
        this.cor = cor;
    }
}
