package components.gamestuff;

import Multiplayer.DataPacket.ClientData;
import Multiplayer.Sender;
import components.*;
import components.unitcapabilities.UnitBuilder;
import components.unitcapabilities.damage.Hitter;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.MoveContollable;
import editor.PropertiesWindow;
import jade.*;
import org.joml.Vector2f;
import util.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static components.gamestuff.MouseControls.getHoldingObject;
import static components.gamestuff.MouseControls.place;
import static jade.Window.get;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    private float debounceTime = 0.2f;
    private float debounce = 0.0f;

    private static boolean Qmove =false;
    public static boolean getQmove(){

        return Qmove;
    }
    public static void setQmove(boolean state){
         Qmove =state;
    }
    private float dt;
    private BlockingQueue<ClientData> requests;
    private Time timer=new Time();
    private float last=0;
    private ArrayList<GameObject> copiedObjects=new ArrayList<>();

    public KeyControls(BlockingQueue<ClientData> requests) {
        this.requests=requests;
    }
    @Override
    public void runningUpdateDraw(){

        dt=timer.getTime()-last;
        debounce -=dt;
        last=timer.getTime();

//        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
//        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
//        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
//        float multiplier = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_V)) {

            Vector2f position = new Vector2f(MouseListener.getWorldX(),MouseListener.getWorldY());
            List<GameObject> obj= Window.getImguiLayer().getMenu().getActiveGameObjects();
            List<Integer> ids=new ArrayList<>();
            for (GameObject go : obj) {
                if(go.allied==get().allied & go.getComponent(MoveContollable.class)!=null){
                    ids.add(go.getUid());
                }

            }

            Sender.sendMove(position,ids);
        }else if(KeyListener.keyBeginPress(GLFW_KEY_Q)){
            Qmove =true;


        }else if(KeyListener.isKeyPressed(GLFW_KEY_ENTER)){
            get().imguiLayer.getMenu().sendMessage();

        }
    }

    @Override
    public void editorUpdateDraw() {
        dt=timer.getTime()-last;
        debounce -=dt;
        last=timer.getTime();

        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        float multiplier = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            if((KeyListener.keyBeginPress(GLFW_KEY_KP_2)||KeyListener.keyBeginPress(GLFW_KEY_KP_0)||KeyListener.keyBeginPress(GLFW_KEY_KP_1)||
                    KeyListener.keyBeginPress(GLFW_KEY_KP_0)||KeyListener.keyBeginPress(GLFW_KEY_KP_3)||KeyListener.keyBeginPress(GLFW_KEY_KP_4)||KeyListener.keyBeginPress(GLFW_KEY_KP_5)||KeyListener.keyBeginPress(GLFW_KEY_KP_6)||
                    KeyListener.keyBeginPress(GLFW_KEY_KP_7)||KeyListener.keyBeginPress(GLFW_KEY_KP_8)||KeyListener.keyBeginPress(GLFW_KEY_KP_9)) && activeGameObject != null){
                int allied=-999999;
                if(KeyListener.keyBeginPress(GLFW_KEY_KP_2)){
                    allied=2;
                }else if(KeyListener.keyBeginPress(GLFW_KEY_KP_0)){
                    allied=0;

                } else if (KeyListener.keyBeginPress(GLFW_KEY_KP_3)) {
                    allied=3;
                }
                else if (KeyListener.keyBeginPress(GLFW_KEY_KP_1)) {
                    allied=1;
                }
                else if (KeyListener.keyBeginPress(GLFW_KEY_KP_4)) {
                    allied=4;
                }
                else if (KeyListener.keyBeginPress(GLFW_KEY_KP_5)) {
                    allied=5;
                }
                else if (KeyListener.keyBeginPress(GLFW_KEY_KP_6)) {
                    allied=6;
                }
                else if (KeyListener.keyBeginPress(GLFW_KEY_KP_7)) {
                    allied=7;
                }
                else if (KeyListener.keyBeginPress(GLFW_KEY_KP_8)) {
                    allied=8;
                }
                else if (KeyListener.keyBeginPress(GLFW_KEY_KP_9)) {
                    allied=9;
                }
                for (GameObject go:activeGameObjects){
                    go.allied=allied;
                    Mortal mort=go.getComponent(Mortal.class);
                    if(mort!=null) {
                        mort.alliedM = allied;
                    }
                    UnitBuilder build=go.getComponent(UnitBuilder.class);
                    if(build!=null) {
                        build.alliedB = allied;
                    }
                    Hitter hit=go.getComponent(Hitter.class);
                    if(hit!=null) {
                        hit.alliedH = allied;
                    }
                }
            }
            if (KeyListener.keyBeginPress(GLFW_KEY_C) && activeGameObject != null) {
                copy(activeGameObjects);

            } else if (KeyListener.keyBeginPress(GLFW_KEY_V) && copiedObjects != null) {
                paste(MouseListener.getWorld(), copiedObjects,true);

            } else if (KeyListener.keyBeginPress(GLFW_KEY_M) && activeGameObjects.size() > 1) {
                List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
                propertiesWindow.clearSelected();
                for (GameObject go : gameObjects) {
                    GameObject copy = go.copy();
                    copy.transform.position.x += Settings.GRID_WIDTH;
                    Window.getScene().addGameObjectToScene(copy);
                    propertiesWindow.addActiveGameObject(copy);
                    if (copy.getComponent(StateMachine.class) != null) {
                        copy.getComponent(StateMachine.class).refreshTextures();
                    }
                }
            }
            if (KeyListener.keyBeginPress(GLFW_KEY_R) && activeGameObject != null) {
                ArrayList<Vector2f> posses=new ArrayList<>();
                for (GameObject go : activeGameObjects) {
                    posses.add(new Vector2f(go.transform.position));
                }
                if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                    if(copiedObjects!=null) {
                        for (Vector2f vec : posses) {
                            paste(vec, copiedObjects, false);
                        }
                    }else{
                        propertiesWindow.clearSelected();
                    }

                } else {
                    GameObject replacement = getHoldingObject();
                    if (replacement != null) {
                        for (Vector2f pos:posses) {
                            replacement.transform.position = pos;
                            replacement.transform.updatePastPos();
                            place();
                        }
                    } else {
                        Window.getImguiLayer().getPropertiesWindow().clearSelected();
                    }


                }
                for (GameObject go :
                        activeGameObjects) {
                    go.destroy();

                }
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
            }
        }else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)||KeyListener.keyBeginPress(GLFW_KEY_BACKSPACE)) {
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
    private void copy(List<GameObject> gameObjects){
        copiedObjects.clear();
        gameObjects.forEach(go->copiedObjects.add(go.copy()));
    }
    private void paste(Vector2f position, ArrayList<GameObject> pasteObjects, Boolean selectit) {
        if (!pasteObjects.isEmpty()) {
            PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
            if(selectit) {
                propertiesWindow.clearSelected();
            }
            List<GameObject> paste = new ArrayList<>();
            pasteObjects.forEach(go -> paste.add(go.copy()));
            float maxX = paste.get(0).transform.position.x, minX = paste.get(0).transform.position.x, maxY = paste.get(0).transform.position.y, minY = paste.get(0).transform.position.y;
            for (int e = 1; e < paste.size(); e++) {//starts at second bc we already initiated with first
                Vector2f pos = paste.get(e).transform.position;
                if (maxX < pos.x) {
                    maxX = pos.x;
                } else if (minX > pos.x) {
                    minX = pos.x;

                }
                if (maxY < pos.y) {
                    maxY = pos.y;
                } else if (minY > pos.y) {
                    minY = pos.y;

                }

            }
            Vector2f center = new Vector2f((float)Math.floor(((minX + maxX) / 2) / Settings.GRID_WIDTH) * Settings.GRID_WIDTH
                    ,(float) Math.floor((minY + maxY) / 2) / Settings.GRID_HEIGHT * Settings.GRID_HEIGHT);
            position.set(((float)Math.floor(position.x) / Settings.GRID_WIDTH) * Settings.GRID_WIDTH
                    ,(float) Math.floor(position.y) / Settings.GRID_HEIGHT * Settings.GRID_HEIGHT);
            for (int e = 0; e < paste.size(); e++) {
                GameObject go = paste.get(e);
                Vector2f pos = go.transform.position;
                pos.set(pos.x - center.x + position.x,
                        pos.y - center.y + position.y);
                go.transform.updatePastPos();

                Window.getScene().addGameObjectToScene(go);
                if(selectit) {
                    propertiesWindow.addActiveGameObject(go);
                }
                if (go.getComponent(StateMachine.class) != null) {
                    go.getComponent(StateMachine.class).refreshTextures();
                }
            }

        }
    }
}
