package physics2d.components;

import components.AnimationState;
import components.Component;
import imgui.ImGui;
import imgui.type.ImString;
import jade.Window;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import physics2d.enums.BodyType;

public class Rigidbody2D extends Component {
    public Rigidbody2D Clone(){
        return new Rigidbody2D();
    }
    public Vector2f velocity = new Vector2f();
    public float angularDamping = 0.8f;
    public float linearDamping = 0.9f;
    public float mass = 0;
    public BodyType bodyType = BodyType.Dynamic;
    public float friction = 0.1f;
    public float angularVelocity = 0.0f;
    public float gravityScale = 1.0f;
    public boolean isSensor = false;

    public boolean fixedRotation = false;
    public boolean continuousCollision = true;

    public transient Body rawBody = null;

    @Override
    public void update(float dt) {
        if (rawBody != null) {
            if (this.bodyType == BodyType.Dynamic || this.bodyType == BodyType.Kinematic) {
                this.gameObject.transform.position.set(
                        rawBody.getPosition().x, rawBody.getPosition().y
                );
                this.gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
                Vec2 vel = rawBody.getLinearVelocity();
                this.velocity.set(vel.x, vel.y);
            } else if (this.bodyType == BodyType.Static) {
                this.rawBody.setTransform(
                        new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y),
                        this.gameObject.transform.rotation
                );
            }
        }
    }

    public void addVelocity(Vector2f forceToAdd) {
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(forceToAdd.x, forceToAdd.y));
        }
    }

    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(velocity.x, velocity.y), rawBody.getWorldCenter());
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (rawBody != null) {
            this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    public void setPosition(Vector2f newPos) {
        if (rawBody != null) {
            rawBody.setTransform(new Vec2(newPos.x, newPos.y), gameObject.transform.rotation);
        }
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) {
            this.rawBody.setAngularVelocity(angularVelocity);
        }
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            this.rawBody.setGravityScale(gravityScale);
        }
    }

    public void setIsSensor() {
        isSensor = true;
        if (rawBody != null) {
            Window.getPhysics().setIsSensor(this);
        }
    }

    public void setNotSensor() {
        isSensor = false;
        if (rawBody != null) {
            Window.getPhysics().setNotSensor(this);
        }
    }

    public float getFriction() {
        return this.friction;
    }

    public boolean isSensor() {
        return this.isSensor;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }
}
