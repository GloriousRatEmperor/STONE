package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.NonPickable;
import jade.Camera;
import jade.GameObject;
import jade.GameObjectDeserializer;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import physics2d.Physics2D;
import renderer.Renderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Scene {

    private Renderer renderer;
    private Camera camera;
    private List<GameObject> bases=new ArrayList();

    private boolean isRunning;
    private List<GameObject> gameObjects;
    private List<GameObject> drawObjects;
    private List<GameObject> pendingObjects;
    private BlockingQueue<GameObject> drawObjectsPending;
    private Physics2D physics2D;
    private File leveltemp;
    private HashMap<Vector2i,Vector3i> floor;

    private SceneInitializer sceneInitializer;
    public double magic = 0;
    public double blood=0;
    public double rock=0;
    public Boolean addmoney(float addblood, float addrock, float addmagic){
        if(-addblood<=blood&-addrock<=rock&-addmagic<=magic) {
            blood += addblood;
            rock += addrock;
            magic += addmagic;
            return true;
        }
        return false;
    }
    public void setMoney(float  setblood, float setrock, float setmagic){
            blood = setblood;
            rock = setrock;
            magic = setmagic;
    }
    public boolean addmoney(Vector3d mineral){
        if(-mineral.x<=blood&-mineral.y<=rock&-mineral.z<=magic) {
            blood += mineral.x;
            rock += mineral.y;
            magic += mineral.z;
            return true;
        }
        return false;
    }

    public void addBase(GameObject gameobject){
        bases.add(gameobject);
    }
    public void removeBase(GameObject gameobject){
        bases.remove(gameobject);
    }
    public GameObject getClosestBase(Vector2f position){
        GameObject base=null;
        double lastDistance=99999999;
        for (GameObject go:bases){
            double  distance = Math.sqrt( Math.pow( go.transform.position.x-position.x,2)+Math.pow( go.transform.position.y-position.y,2));
            if (distance<lastDistance){
                lastDistance=distance;
                base=go;
            }
        }
        return base;

    }

    public Scene(SceneInitializer sceneInitializer, File leveltemp) {
        this.sceneInitializer = sceneInitializer;
        this.physics2D = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.drawObjects = new ArrayList<>();
        this.drawObjectsPending = new ArrayBlockingQueue<>(5000);
        this.pendingObjects = new ArrayList<>();
        this.isRunning = false;
        this.leveltemp = leveltemp;
    }

    public Physics2D getPhysics() {
        return this.physics2D;
    }

    public void init() {
        this.camera = new Camera(new Vector2f(0, 0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.start();
            this.physics2D.add(go);
            //drawObjectsPending.add(go);
            drawObjects.add(go);
            this.renderer.add(go);
        }
        isRunning = true;
    }
    public void setFloor(HashMap<Vector2i, Vector3i> newmap){
        floor=newmap;
        renderer.setFloor(floor);
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);

        } else {
            pendingObjects.add(go);
        }
    }

    public void destroy() {
        for (GameObject go : gameObjects) {
            go.destroy();
        }
    }

    public <T extends Component> GameObject getGameObjectWith(Class<T> clazz) {
        for (GameObject go : gameObjects) {
            if (go.getComponent(clazz) != null) {
                return go;
            }
        }

        return null;
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }
    public List<GameObject> getDrawObjects() {
        return this.drawObjects;
    }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return result.orElse(null);
    }

    public ArrayList<GameObject> getGameObjects(List<Integer> gameObjectIds) {
        ArrayList<GameObject> selected = new ArrayList<>();
        for (Integer gameObjectId : gameObjectIds) {
            GameObject pickedObj = getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                selected.add(pickedObj);
            }
        }
        return selected;
    }


    public void editorUpdate(float dt) {
        //never called lol
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editorUpdate(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
        for (GameObject go : gameObjects) {
            Transform tr = go.getComponent(Transform.class);
            if (tr != null) {
                tr.drawPos.set(tr.position);
            }
        }

        for (GameObject go : pendingObjects) {
            gameObjects.add(go);
            drawObjectsPending.add(go);
            go.start();
            this.physics2D.add(go);
        }
        pendingObjects.clear();
    }

    public void editorUpdateDraw() throws InterruptedException {
        //misleading esp with editorUpdate bc this does everything, like this for possibility of splitting things into the thread but probably never going to do it

        this.camera.adjustProjection();

        for (GameObject go : pendingObjects) {
            gameObjects.add(go);
            drawObjects.add(go);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
        pendingObjects.clear();
        for(int i = 0; i<drawObjects.size();i++) {
            GameObject go = drawObjects.get(i);
            if (!go.isDead()) {
                Transform tr = go.getComponent(Transform.class);
                if (tr != null) {
                    if (tr.position.x != 0f) {
                            tr.drawPos.set(tr.position);
                        }
                    }

                go.editorUpdateDraw();

                go.updateDraw();
            } else {
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                drawObjects.remove(go);
                gameObjects.remove(go);
            }
        }
        //not that there should be anything I think but like I dunno...
        while (!drawObjectsPending.isEmpty()) {
            GameObject go=drawObjectsPending.take();
            drawObjects.add(go);
            this.renderer.add(go);
        }

    }

    public GameObject getGameObject(String gameObjectName) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.name.equals(gameObjectName))
                .findFirst();
        return result.orElse(null);
    }
    public void soloUpdate(float dt) {
        this.camera.adjustProjection();

        this.physics2D.update(dt);

        for (int i=0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
            Transform tr = go.getComponent(Transform.class);
            if (tr != null) {
                tr.drawPos.set(tr.position);
            }
        }
        for (GameObject go : pendingObjects) {
            gameObjects.add(go);
            drawObjectsPending.add(go);
            go.start();


            //this.renderer.add(go);
            this.physics2D.add(go);

            Transform tr = go.getComponent(Transform.class);
            if (tr != null) {
                tr.drawPos.set(tr.position);
            }
        }
        pendingObjects.clear();

    }
    public void update(float dt,boolean complete) {
        if(complete){
            for (GameObject go : gameObjects) {
                Transform tr = go.getComponent(Transform.class);
                if (tr != null) {
                    tr.updatePastPos();
                }
            }
        }
        this.physics2D.update(dt);

        for (int i=0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }

        for (GameObject go : pendingObjects) {
            gameObjects.add(go);
            drawObjectsPending.add(go);
            go.start();
            //this.renderer.add(go);
            this.physics2D.add(go);
        }
        pendingObjects.clear();
    }
    public void visualUpdate(float fractionPassed,boolean running) throws InterruptedException { //fractionpassed=dt/physicsframetime

        this.camera.adjustProjection();
        if(!running) {
            for (GameObject go : pendingObjects) {
                gameObjects.add(go);
                drawObjects.add(go);
                go.start();
                this.renderer.add(go);
                this.physics2D.add(go);
            }
            pendingObjects.clear();
        }
        for (int i=0; i < drawObjects.size(); i++) {
            GameObject go = drawObjects.get(i);
            if(!go.isDead()) {
                Transform tr = go.getComponent(Transform.class);
                if (tr != null) {
//                    if (tr.position.x != 0f) {
                        if(running) {
                            tr.updateDrawPos(fractionPassed);
                        }else{
                            tr.drawPos.set(tr.position);
                        }
//                    }
                }
                if (running) {
                    go.runningUpdateDraw();

                } else {
                    go.editorUpdateDraw();

                }
                go.updateDraw();
            }else{
                drawObjects.remove(go);
            }
        }
        while (!drawObjectsPending.isEmpty()) {
             GameObject go=drawObjectsPending.take();
            drawObjects.add(go);
            this.renderer.add(go);
        }
    }

    public void render(Boolean picking) {
        this.renderer.render(picking);
    }
    public void renderFloor() {
        this.renderer.renderFloor();
    }

    public Camera camera() {
        return this.camera;
    }

    public void imgui() {
        this.sceneInitializer.imgui();
    }
    public GameObject createGameObject(String name,Vector2f position) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform(position));
        go.transform = go.getComponent(Transform.class);
        return go;
    }
    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void save(String file) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        try {
            FileWriter writer;
            if (file!=null) {
                writer = new FileWriter("ZlevelSaves/"+file);
            }else{
                writer = new FileWriter(leveltemp);
            }

            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj : this.gameObjects) {
                if (obj.doSerialization()) {
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(file!=null) {
            save(null);
        }
    }

    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        String inFile = "";
        try {

            inFile = new String(Files.readAllBytes(Paths.get(leveltemp.getPath())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i=0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for (Component c : objs[i].getAllComponents()) {
                    if (c.getUid() > maxCompId) {
                        maxCompId = c.getUid();
                    }
                }
                if (objs[i].getUid() > maxGoId) {
                    maxGoId = objs[i].getUid();
                }
            }

            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
        }
    }
}
