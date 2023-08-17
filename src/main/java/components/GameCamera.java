package components;

import jade.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera gameCamera;
    private Vector2f clickOrigin;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;
    private float lerpTime = 0.0f;
    private boolean reset = false;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;

    private Vector4f skyColor = new Vector4f(92.0f / 255.0f, 148.0f / 255.0f, 252.0f / 255.0f, 1.0f);
    private Vector4f undergroundColor = new Vector4f(0, 0, 0, 1);

    public GameCamera(Camera gameCamera) {
        this.gameCamera = gameCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.gameCamera.clearColor.set(skyColor);
        this.undergroundYLevel = this.gameCamera.position.y -
                this.gameCamera.getProjectionSize().y - this.cameraBuffer;
    }

    @Override
    public void update(float dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            this.clickOrigin = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            gameCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }


        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / gameCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            gameCamera.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            reset = true;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            gameCamera.position.add(-0.1f*this.gameCamera.getZoom(),0);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            gameCamera.position.add(0,0.1f*this.gameCamera.getZoom());
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            gameCamera.position.add(0,-0.1f*this.gameCamera.getZoom());
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            gameCamera.position.add(0.1f*this.gameCamera.getZoom(),0);
        }
        if (reset) {
            gameCamera.position.lerp(new Vector2f(), lerpTime);
            gameCamera.setZoom(this.gameCamera.getZoom() +
                    ((1.0f - gameCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(gameCamera.position.x) <= 5.0f &&
                    Math.abs(gameCamera.position.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                gameCamera.position.set(0f, 0f);
                this.gameCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
