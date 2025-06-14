package components.gamestuff;

import Multiplayer.DataPacket.ClientData;
import Multiplayer.Sender;
import components.Component;
import components.unitcapabilities.NonPickable;
import components.unitcapabilities.defaults.MoveContollable;
import editor.PropertiesWindow;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector2i;
import renderer.DebugDraw;
import renderer.PickingTexture;
import scenes.Scene;
import util.Settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import static components.gamestuff.KeyControls.setQmove;
import static editor.Menu.activateCast;
import static editor.Menu.casting;
import static jade.Window.get;
import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    static GameObject holdingObject = null;
    private float debounceTime = 0.05f;
    private Time timer=new Time();
    private float last=0;
    private ArrayList<Vector2f> places= new ArrayList<>(10);
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    private BlockingQueue<ClientData> requests;
    public MouseControls( BlockingQueue<ClientData> requests){
    this.requests=requests;
    }
    public static GameObject getHoldingObject(){
        return holdingObject;
    }
    public void pickupObject(GameObject go) {
        if (this.holdingObject != null) {
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        holdingObject.setNoSerialize();
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(0.8f, 0.8f, 0.8f, 0.5f);
        this.holdingObject.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(go);
    }

    public static void place() {

        GameObject newObj = holdingObject.copy();
        newObj.setYesSerialize();
        if (newObj.getComponent(StateMachine.class) != null) {
            newObj.getComponent(StateMachine.class).refreshTextures();
        }
        newObj.getComponent(SpriteRenderer.class).setColor(1, 1, 1, 1);
        newObj.removeComponent(NonPickable.class);
        Window.getScene().addGameObjectToScene(newObj);
    }

    @Override
    public void runningUpdateDraw(){
        debounce -=timer.getTime()-last;
        last=timer.getTime();

        PickingTexture pickingTexture = Window.getImguiLayer().getMenu().getPickingTexture();
        Scene currentScene = Window.getScene();
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)&& debounce < -debounceTime) {
            debounce=debounceTime;
            Vector2f position = new Vector2f(MouseListener.getWorldX(),MouseListener.getWorldY());
            List<GameObject> obj= Window.getImguiLayer().getMenu().getActiveGameObjects();
            List<Integer> ids=new ArrayList<>();
            for (GameObject go : obj) {
                if(go.allied==get().allied & go.getComponent(MoveContollable.class)!=null){

                    ids.add(go.getUid());
                }

            }
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = currentScene.getGameObjectByName(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                Sender.sendMove(position,ids,gameObjectId);
            }else {

                Sender.sendMove(position, ids);
            }
        }
        else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            setQmove(false);
            if(casting){
                activateCast();
                debounce=debounceTime*4;
            }else {
                int x = (int) MouseListener.getScreenX();
                int y = (int) MouseListener.getScreenY();
                int gameObjectId = pickingTexture.readPixel(x, y);
                GameObject pickedObj = currentScene.getGameObjectByName(gameObjectId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    Window.getImguiLayer().getMenu().setActiveGameObject(pickedObj);
                } else if (pickedObj == null && !MouseListener.isDragging()) {
                    Window.getImguiLayer().getMenu().clearSelected();
                }
                this.debounce = debounceTime / 2;
            }
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)&& debounce < 0) {

            if(casting){
                activateCast();
                debounce=debounceTime*4;
            }else {
                if (!boxSelectSet) {
                    Window.getImguiLayer().getMenu().clearSelected();
                    boxSelectStart = MouseListener.getScreen();
                    boxSelectSet = true;
                }
                boxSelectEnd = MouseListener.getScreen();
                Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
                Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
                Vector2f halfSize =
                        (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
                DebugDraw.addBox2D(
                        (new Vector2f(boxSelectStartWorld)).add(halfSize),
                        new Vector2f(halfSize).mul(2.0f),
                        0.0f);
            }
        } else if (boxSelectSet) {
            //actually mass box selects shit I thinketh

            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId : gameObjectIds) {
                uniqueGameObjectIds.add((int)objId);
            }

            for (Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObj = Window.getScene().getGameObjectByName(gameObjectId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    Window.getImguiLayer().getMenu().addActiveGameObject(pickedObj);
                }
            }
        }
    }
    @Override
    public void editorUpdateDraw() {
        debounce -=timer.getTime()-last;
        last=timer.getTime();
        PickingTexture pickingTexture = Window.getImguiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene();

        if (holdingObject != null) {
            float x = MouseListener.getWorldX();
            float y = MouseListener.getWorldY();
            holdingObject.transform.position.x = ((int)Math.floor(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;



                if (MouseListener.isDragging() &&
                    !blockInSquare(holdingObject.transform.position.x - halfWidth,
                            holdingObject.transform.position.y - halfHeight)  &&
                        !(places.contains(new Vector2f(holdingObject.transform.position.x,holdingObject.transform.position.y)))) {
                    place();
                    places.add(new Vector2f(holdingObject.transform.position.x,holdingObject.transform.position.y));
                    if (places.size()==3) {
                        places.remove(0);
                    }
                } else if (!MouseListener.isDragging() && debounce < 0 &&
                !blockInSquare(holdingObject.transform.position.x - halfWidth,
                        holdingObject.transform.position.y - halfHeight)  &&
                        !(places.contains(new Vector2f(holdingObject.transform.position.x,holdingObject.transform.position.y)))) {
                    place();
                    places.add(new Vector2f(holdingObject.transform.position.x,holdingObject.transform.position.y));
                    if (places.size()==3) {
                        places.remove(0);
                    }
                    debounce = debounceTime;
                }
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                discardHoldingObject();

            }
        } else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = currentScene.getGameObjectByName(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                Window.getImguiLayer().getPropertiesWindow().setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
            }
            this.debounce =debounceTime/2;
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!boxSelectSet) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize =
                    (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D(
                    (new Vector2f(boxSelectStartWorld)).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f),
                    0.0f);
        } else if (boxSelectSet) {
            //actually mass box selects shit I thinketh
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId : gameObjectIds) {
                uniqueGameObjectIds.add((int)objId);
            }

            for (Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObj = Window.getScene().getGameObjectByName(gameObjectId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    Window.getImguiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }

    private boolean blockInSquare(float x, float y) {
        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
        float[] gameObjectIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < gameObjectIds.length; i++) {
            if (gameObjectIds[i] >= 0) {
                GameObject pickedObj = Window.getScene().getGameObjectByName((int)gameObjectIds[i]);
                if (pickedObj.getComponent(NonPickable.class) == null) {
                    return true;
                }
            }
        }



        return false;
    }
    public static void discardHoldingObject(){
        if(holdingObject!=null) {
            holdingObject.destroy();
            holdingObject = null;
        }
    }
}
