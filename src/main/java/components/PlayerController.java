package components;

import jade.GameObject;
import jade.KeyListener;
import jade.Prefabs;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import physics2d.Physics2D;
import physics2d.RaycastInfo;
import physics2d.components.PillboxCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;
import renderer.DebugDraw;
import scenes.LevelEditorSceneInitializer;
import scenes.LevelSceneInitializer;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    public PlayerController Clone(){
        return new PlayerController();
    }
    //PlayerController is created when a new mario is made otherwie it is included in level.txt
    //THIS FILE WAS BUTCHERED
    private enum PlayerState {
        Small,
        Big,
        Fire,
        Invincible
    }

    public float walkSpeed = 1.9f;
    public float jumpBoost = 1.0f;
    public float jumpImpulse = 3.0f;
    public float slowDownForce = 0.05f;
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private PlayerState playerState = PlayerState.Small;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;
    private transient Rigidbody2D rb;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead = false;
    private transient int enemyBounce = 0;

    private transient float hurtInvinciAbilityTimeLeft = 0;
    private transient float hurtInvinciAbilityTime = 1.4f;
    private transient float deadMaxHeight = 0;
    private transient float deadMinHeight = 0;
    private transient boolean deadGoingUp = true;
    private transient float blinkTime = 0.0f;
    private transient SpriteRenderer spr;

    private transient boolean playWinAnimation = false;
    private transient float timeToCastle = 4.5f;
    private transient float walkTime = 2.2f;

    @Override
    public void start() {
        this.spr = gameObject.getComponent(SpriteRenderer.class);
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);
    }

    @Override
    public void update(float dt) {
        if (playWinAnimation) {
            gameObject.transform.scale.x = -0.25f;
            gameObject.transform.position.y -= dt;
            stateMachine.trigger("stopRunning");
            stateMachine.trigger("stopJumping");
            return;
        }

        if (isDead) {
            if (this.gameObject.transform.position.y < deadMaxHeight && deadGoingUp) {
                this.gameObject.transform.position.y += dt * walkSpeed / 2.0f;
            } else if (this.gameObject.transform.position.y >= deadMaxHeight && deadGoingUp) {
                deadGoingUp = false;
            } else if (!deadGoingUp && gameObject.transform.position.y > deadMinHeight) {
                this.rb.setBodyType(BodyType.Kinematic);
                this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
                this.velocity.y += this.acceleration.y * dt;
                this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
                this.rb.setVelocity(this.velocity);
                this.rb.setAngularVelocity(0);
            } else if (!deadGoingUp && gameObject.transform.position.y <= deadMinHeight) {
                gameObject.destroy();
            }
            return;
        }

        if (hurtInvinciAbilityTimeLeft > 0) {
            hurtInvinciAbilityTimeLeft -= dt;
            blinkTime -= dt;

            if (blinkTime <= 0) {
                blinkTime = 0.2f;
                if (spr.getColor().w == 1) {
                    spr.setColor(1, 1, 1, 0);
                } else {
                    spr.setColor(1, 1, 1, 1);
                }
            } else {
                if (spr.getColor().w == 0) {
                    spr.setColor(1, 1, 1, 1);
                }
            }
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {

            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x -= slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0) {
                this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);
            }

            if (this.velocity.x == 0) {
                this.stateMachine.trigger("stopRunning");
            }
        }
        if (KeyListener.keyBeginPress(GLFW_KEY_E)) {
            Vector2f position = new Vector2f(this.gameObject.transform.position)
                    .add(this.gameObject.transform.scale.x > 0
                    ? new Vector2f(0.26f, 0)
                    : new Vector2f(-0.26f, 0));
            GameObject fireball = Prefabs.generateFireball(position);
            fireball.getComponent(Fireball.class).goingRight =
                    this.gameObject.transform.scale.x > 0;
            Window.getScene().addGameObjectToScene(fireball);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_UP) && (jumpTime > 0 || groundDebounce > 0)) {
            if (( groundDebounce > 0) && jumpTime == 0) {
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN) && (jumpTime > 0 || groundDebounce > 0)) {
            if ((groundDebounce > 0) && jumpTime == 0) {
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                this.velocity.y = -jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.velocity.y = -((jumpTime / 2.2f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        }else {
            this.velocity.y = 0;
            this.acceleration.y = 0;
            groundDebounce = groundDebounceTime;
        }


        this.velocity.x += this.acceleration.x * dt;
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0);


            stateMachine.trigger("stopJumping");

    }

    public void setPosition(Vector2f newPos) {
        this.gameObject.transform.position.set(newPos);
        this.rb.setPosition(newPos);
    }

    public void powerup() {
        if (playerState == PlayerState.Small) {
            playerState = PlayerState.Big;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
            gameObject.transform.scale.y = 0.42f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null) {
                jumpBoost *= bigJumpBoostFactor;
                walkSpeed *= bigJumpBoostFactor;
                pb.setHeight(0.42f);
            }
        } else if (playerState == PlayerState.Big) {
            playerState = PlayerState.Fire;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
        }

        stateMachine.trigger("powerup");
    }

    public void playWinAnimation(GameObject flagpole) {
        if (!playWinAnimation) {
            playWinAnimation = true;
            velocity.set(0.0f, 0.0f);
            acceleration.set(0.0f, 0.0f);
            rb.setVelocity(velocity);
            rb.setIsSensor();
            rb.setBodyType(BodyType.Static);
            gameObject.transform.position.x = flagpole.transform.position.x;
            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
            AssetPool.getSound("assets/sounds/flagpole.ogg").play();
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        if (isDead) return;

        if (collidingObject.getComponent(Ground.class) != null) {
            if (Math.abs(contactNormal.x) > 0.8f) {
                this.velocity.x = 0;
            } else if (contactNormal.y > 0.8f) {
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }

    public void enemyBounce() {
        this.enemyBounce = 8;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public boolean isHurtInvincible() {
        return this.hurtInvinciAbilityTimeLeft > 0 || playWinAnimation;
    }

    public boolean isInvincible() {
        return this.playerState == PlayerState.Invincible ||
                this.hurtInvinciAbilityTimeLeft > 0 || playWinAnimation;
    }

    public void die() {
        this.stateMachine.trigger("die");
        if (this.playerState == PlayerState.Small) {
            this.velocity.set(0, 0);
            this.acceleration.set(0, 0);
            this.rb.setVelocity(new Vector2f());
            this.isDead = true;
            this.rb.setIsSensor();
            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
            AssetPool.getSound("assets/sounds/mario_die.ogg").play();
            deadMaxHeight = this.gameObject.transform.position.y + 0.3f;
            this.rb.setBodyType(BodyType.Static);
            if (gameObject.transform.position.y > 0) {
                deadMinHeight = -0.25f;
            }
        } else if (this.playerState == PlayerState.Big) {
            this.playerState = PlayerState.Small;
            gameObject.transform.scale.y = 0.25f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null) {
                jumpBoost /= bigJumpBoostFactor;
                walkSpeed /= bigJumpBoostFactor;
                pb.setHeight(0.25f);
            }
            hurtInvinciAbilityTimeLeft = hurtInvinciAbilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
        } else if (playerState == PlayerState.Fire) {
            this.playerState = PlayerState.Big;
            hurtInvinciAbilityTimeLeft = hurtInvinciAbilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
        }
    }

    public boolean hasWon() {
        return false;
    }

    public boolean isSmall() {
        return this.playerState == PlayerState.Small;
    }
}
