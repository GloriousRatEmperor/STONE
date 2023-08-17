package components;

import Multiplayer.ClientData;
import editor.PropertiesWindow;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import util.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    private float debounceTime = 0.2f;
    private float debounce = 0.0f;
    private BlockingQueue<ClientData> requests;
    private Thread clientThread;
    public KeyControls(Thread clientThread, BlockingQueue<ClientData> requests) {
        this.clientThread=clientThread;
        this.requests=requests;
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        float multiplier = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_V) && activeGameObject != null) {

            for(int e=0;e<activeGameObjects.size();e++) {
                GameObject go=activeGameObjects.get(0);
                propertiesWindow.removeActiveGameObject(go);
                GameObject newObj = go.copy();
                Window.getScene().addGameObjectToScene(newObj);
                newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
                propertiesWindow.addActiveGameObject(newObj);
                if (newObj.getComponent(StateMachine.class) != null) {
                    newObj.getComponent(StateMachine.class).refreshTextures();
                }
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
                if (copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }
            }
        } else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)||KeyListener.keyBeginPress(GLFW_KEY_BACKSPACE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex--;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex++;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x -= Settings.GRID_HEIGHT * multiplier;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x += Settings.GRID_HEIGHT * multiplier;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        }
    }

}
