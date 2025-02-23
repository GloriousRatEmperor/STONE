package components.gamestuff;

import components.Component;
import jade.Camera;
import jade.KeyListener;
import jade.MouseListener;
import jade.Time;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private float dragDebounce = 0.032f;

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;
    private Time timer=new Time();
    private float last=0;
    private float dt;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdateDraw() {
        dt=timer.getTime()-last;
        last=timer.getTime();
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            this.clickOrigin = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            reset = true;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            levelEditorCamera.position.add(-0.1f*this.levelEditorCamera.getZoom(),0);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            levelEditorCamera.position.add(0,0.1f*this.levelEditorCamera.getZoom());
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            levelEditorCamera.position.add(0,-0.1f*this.levelEditorCamera.getZoom());
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            levelEditorCamera.position.add(0.1f*this.levelEditorCamera.getZoom(),0);
        }
        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f &&
                    Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(2.0f);
                reset = false;
            }
        }
    }

}
