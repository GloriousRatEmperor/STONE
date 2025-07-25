package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.SubComponents.SubComponent;
import components.gamestuff.Deserializer.ComponentDeserializer;
import components.gamestuff.Deserializer.GameObjectDeserializer;
import components.gamestuff.Deserializer.SubComponentDeserializer;
import components.gamestuff.GameCamera;
import components.gamestuff.PlayerBot;
import components.unitcapabilities.NonPickable;
import jade.Camera;
import jade.GameObject;
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
import java.util.concurrent.ConcurrentLinkedQueue;

import static jade.Window.get;

public class Scene {

    private Renderer renderer;

    private Camera camera;

    ConcurrentLinkedQueue<Integer> requestIds =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<GameObject> reuestObjects=new ConcurrentLinkedQueue<>();
    private boolean isRunning;
    private List<GameObject> gameObjects;
    private List<GameObject> drawObjects;
    private List<GameObject> pendingObjects;
    private BlockingQueue<GameObject> drawObjectsPending;
    private Physics2D physics2D;
    private File leveltemp;
    private int allied;
    private ArrayList<PlayerBot> playerBots=new ArrayList<>();
    private HashMap<Vector2i,Vector3i> floor;

    private SceneInitializer sceneInitializer;
    private ArrayList<Vector3d> money=new ArrayList<>();
    public Boolean addmoney(float addblood, float addrock, float addmagic,int player){
        Vector3d moneys=money.get(player-1);
        if(-addblood<=moneys.x&-addrock<=moneys.y&-addmagic<=moneys.z) {
            moneys.add(addblood,addrock,addmagic);
            return true;
        }
        return false;
    }
    public Boolean haveMoney(float addblood, float addrock, float addmagic,int player){
        if(player>money.size()){
            return false;
        }
        Vector3d moneys=money.get(player-1);
        if(-addblood<=moneys.x&-addrock<=moneys.y&-addmagic<=moneys.z) {
            return true;
        }
        return false;
    }
    public Vector3d getmoney(int player){
        return money.get(player-1);
    }
    public void setAllied(int allied){
        this.allied=allied;
    }
    public int getAllied(){
        return allied;
    }
    public double getRock(){
        return money.get(allied-1).y;
    }
    public double getBlood(){
        return money.get(allied-1).x;
    }
    public double getMagic(){
        return money.get(allied-1).z;
    }
    public double getEnemyRock(){
        return money.get(money.size()-allied).y;
    }
    public double getEnemyBlood(){
        return money.get(money.size()-allied).x;
    }
    public double getEnemyMagic(){
        return money.get(money.size()-allied).z;
    }
    public LevelEditorSceneInitializer getLevelEditorSceneInitiallizer(){
        return (LevelEditorSceneInitializer) sceneInitializer;
    }
    public void setMoney(float  setblood, float setrock, float setmagic,int player){
        Vector3d moneys=money.get(player-1);
        moneys.set(setblood,setrock,setmagic);
    }
    public void setAllMoney(float  setblood, float setrock, float setmagic){
        for (Vector3d moneys :money) {
            moneys.set(setblood, setrock, setmagic);
        }
    }
    public boolean addmoney(Vector3d mineral,int player){
        Vector3d moneys=money.get(player-1);
        if(-mineral.x<=moneys.x&-mineral.y<=moneys.y&-mineral.z<=moneys.z) {
            moneys.x += mineral.x;
            moneys.y+= mineral.y;
            moneys.z += mineral.z;
            return true;
        }
        return false;
    }



    public Scene(SceneInitializer sceneInitializer, File leveltemp) {
        this.sceneInitializer = sceneInitializer;
        this.physics2D = new Physics2D();
        if(get().hasDrawThread){
            this.renderer = new Renderer();
            this.drawObjects = new ArrayList<>();
        }
        this.gameObjects = new ArrayList<>();

        this.drawObjectsPending = new ArrayBlockingQueue<>(5000);
        this.pendingObjects = new ArrayList<>();
        this.isRunning = false;
        this.leveltemp = leveltemp;
    }

    public Physics2D getPhysics() {
        return this.physics2D;
    }
    public void addPlayerBot(PlayerBot bot){
        if(!get().hasDrawThread) {
            playerBots.add(bot);
            bot.setGameObjects(gameObjects);
        }
    }
    public void init() {
        if(get().hasDrawThread) {
            this.camera = new Camera(new Vector2f(0, 0));
            this.sceneInitializer.loadResources(this);
        }
        this.sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);

            go.start();
            this.physics2D.add(go);
            //drawObjectsPending.add(go);
            if(get().hasDrawThread){
                drawObjects.add(go);
                this.renderer.add(go);
            }

        }
        isRunning = true;
    }
    public void setFloor(HashMap<Vector2i, Vector3i> newmap){
        floor=newmap;
        if(renderer!=null){
            renderer.setFloor(floor);
        }

    }
    public void initiatePlayers(int playeramount){
        for (int i=0;i<playeramount;i++){
            money.add(new Vector3d());
        }
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);

        } else {
            pendingObjects.add(go);
        }
    }
    public void addDrawObjecttoScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);

        } else {
            drawObjectsPending.add(go);
        }
    }

    public void destroy() {
        synchronized(gameObjects) {
            for (GameObject go : gameObjects) {
                go.destroy();
            }
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

    public List<GameObject> getGameObjectsDraw() {
        return this.gameObjects;
    }
    public List<GameObject> getDrawObjects() {
        return this.drawObjects;
    }
    //for drawthread use
    public GameObject getGameObjectByName(int gameObjectId) {

        Optional<GameObject> result = this.drawObjects.stream()
                    .filter(gameObject -> gameObject.getUid() == gameObjectId)
                    .findFirst();
            return result.orElse(null);

    }
    //for drawthread use
    public ArrayList<GameObject> getGameObjectsDraw(List<Integer> gameObjectIds) {
        ArrayList<GameObject> selected = new ArrayList<>();
        for (Integer gameObjectId : gameObjectIds) {
            GameObject pickedObj = getGameObjectByName(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                selected.add(pickedObj);
            }
        }
        return selected;
    }
    //for non-drawthread use
    public GameObject getGameObjectRunning(int gameObjectId) {

        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return result.orElse(null);

    }
    //for non-drawthread use
    public ArrayList<GameObject> getGameObjectsRunning(List<Integer> gameObjectIds) {
        ArrayList<GameObject> selected = new ArrayList<>();
        for (Integer gameObjectId : gameObjectIds) {
            GameObject pickedObj = getGameObjectRunning(gameObjectId);
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
                            tr.drawPos.set(tr.position);

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

    public GameObject getGameObjectByName(String gameObjectName) {
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
        for(PlayerBot bot:playerBots){
            bot.update(dt);
        }
        for (int i=0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead()) {
                gameObjects.remove(i);

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
                this.renderer.destroyGameObject(go);
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
                .registerTypeAdapter(SubComponent.class, new SubComponentDeserializer())
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
                if(obj.getComponent(GameCamera.class)==null) {
                    if (obj.doSerialization()) {
                        objsToSerialize.add(obj);
                    }
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
                .registerTypeAdapter(SubComponent.class, new SubComponentDeserializer())
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
