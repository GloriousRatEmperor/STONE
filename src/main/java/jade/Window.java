package jade;

import Multiplayer.ClientData;
import Multiplayer.ServerData;
import components.MouseControls;
import components.ServerInputs;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import physics2d.Physics2D;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.LevelSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;
import util.UniTime;
import util.Unit;
import util.Variables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    public Thread clientThread;
    public static ServerData startData;
    public BlockingQueue<ClientData> requests;
    public BlockingQueue<ServerData> responses;
    private int width, height;

    private File leveltemp;
    public int allied=0;
    private boolean end=false;
    public static int playerAmount;
    private boolean endPlay=false;
    public ImGuiLayer imguiLayer;

    public Framebuffer framebuffer;
    public static HashMap<Vector2i, Vector3i> floor;


    public static boolean runtimePlaying = false;
    private float beginTime;
    private float endTime;


    public static ArrayList<Shader> Shaders = new ArrayList<Shader>();
    public static boolean newFloor=false;

    private float dt;
    private final int nano=1000000000;
    boolean start=false;
    boolean ready=false;

    private int physicsTimes;
    private float fractionPassed;
    public static float physicsStep;
    private float lastPhysics;
    private float starttime;
    private static Window window = null;

    private long audioContext;
    private long audioDevice;
    public static boolean scened=false;

    private static Scene currentScene;
    public Boolean debugging;
    public static boolean casting;
    private static List<Integer> targetIds;
    private static int targetAbility;
    public static void setAllied(int allied){
        get().allied=allied;
    }

    private Window() {

        this.width = 1920;
        this.height = 1080;
        EventSystem.addObserver(this);

    }
    public static void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }

        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer, get().leveltemp);
        currentScene.load();
        currentScene.init();
        currentScene.start();
        scened=true;

    }
    public static void ChangeLevel(String lvlname){
        FileUtil.copyFile("ZlevelSaves/"+lvlname,get().leveltemp.getPath(),true);

    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Physics2D getPhysics() { return currentScene.getPhysics(); }

    public static Scene getScene() {
        return currentScene;
    }

    public void run(Boolean debugging) throws NoSuchFieldException, InterruptedException {
        this.debugging=debugging;
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        loop();

    }
    private void cleanTemps(File dir){
        List<File> cleans =getFilesOF(dir.getAbsolutePath());
        int deletionTimer=300000;
        for (File f:cleans){
            if(f.lastModified()<System.currentTimeMillis()-deletionTimer){
                if(!f.delete()){
                    System.out.println("ÜGH deletion failed, perhaps somathing has been playing for like "+deletionTimer/(1000*60)+" minutes");
                }
            }

        }
    }
    private List<File> getFilesOF(String directory) {
        File directoryFile = new File(directory);

        if(!directoryFile.isDirectory()) {
            throw new IllegalArgumentException("path must be a directory");
        }

        List<File> results = new ArrayList<>();

        results.addAll(Arrays.asList(Objects.requireNonNull(directoryFile.listFiles())));
        return results;
    }
    public void init(Boolean debugging) {

    }
    public void stop(){
        Window.changeScene(new LevelEditorSceneInitializer(clientThread, requests, responses));

    }
    public void begin() {
        MouseControls.discardHoldingObject();
        start=false;
        ServerInputs inputs= currentScene.getGameObject("LevelEditor").getComponent(ServerInputs.class);
        long StartTime=inputs.getStartTime();
        UniTime.set(StartTime);
        inputs.setTime(0f);
        beginTime=0f;
        lastPhysics = 0f;
        UniTime.setFrame(lastPhysics);
        physicsTimes=0;

        allied= startData.getIntValue();
        getImguiLayer().getMenu().allied=allied;
        GameObject.setCounter(startData.getIdCounter());
        try {
            FileWriter writer = new FileWriter(leveltemp);
            String startLevel=startData.getstrValue();
            writer.write(startLevel);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentScene.load();


        Window.changeScene(new LevelSceneInitializer(clientThread, requests, responses));

        ServerInputs newInputs= currentScene.getGameObject("LevelEditor").getComponent(ServerInputs.class);
        Scene scene=Window.getScene();
        scene.setFloor(floor);
        scene.initiatePlayers(startData.getPlayerAmount());
        this.playerAmount=startData.getPlayerAmount();
        scene.setAllied(allied);
        newInputs.setTime(0f);
        Variables.start();
        this.runtimePlaying = true;
        starttime=UniTime.getExact();

        startData=null;
    }



    public void loop() throws NoSuchFieldException, InterruptedException {
         beginTime = UniTime.getExact();
         dt = -1.0f;
         physicsTimes=0;
         fractionPassed=0f;
         physicsStep=1/60f;
         lastPhysics=UniTime.getExact();


        Thread screen = new Thread(new Runnable() {

            String title = "Stone Throne";
            private long glfwWindow;
            private Shader defaultShader;
            private Shader groundShader;
            private Shader pickingShader;
            private Shader invertedShader;
            private Shader BlackShader;
            private PickingTexture pickingTexture;

            public void renderGame() throws NoSuchFieldException, InterruptedException {
                // Poll events
                glfwPollEvents();

                // Render pass 1. Render to picking texture
                glDisable(GL_BLEND);
                pickingTexture.enableWriting();

                glViewport(0, 0, 3840, 2160);
                glClearColor(0, 0, 0, 0);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                Renderer.bindShader(pickingShader);

                currentScene.render(true);
                imguiLayer.update(currentScene, runtimePlaying);

                pickingTexture.disableWriting();
                glEnable(GL_BLEND);

                // Render pass 2. Render actual game
                DebugDraw.beginFrame();

                framebuffer.bind();


                if(runtimePlaying) {
                    Renderer.bindShader(groundShader);
                    currentScene.renderFloor();
                }else {
                    Vector4f clearColor = currentScene.camera().clearColor;
                    glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
                    glClear(GL_COLOR_BUFFER_BIT);
                }

                Renderer.bindShader(defaultShader);
                if(runtimePlaying) {

                    currentScene.visualUpdate(fractionPassed, runtimePlaying);
                }else{
                    //misleading esp with editor update bc it does everything, like this for possibility of splitting things into the thread but probably never going to do it
                    currentScene.editorUpdateDraw();
                }

                currentScene.render(false);
                DebugDraw.draw();

                framebuffer.unbind();

//                      this could be instead of imguiLayer.update(currentScene, runtimePlaying); if no imgui
//                    glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer.getFboID());
//                    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
//                    glBlitFramebuffer(0, 0, framebuffer.width, framebuffer.height, 0, 0, width, height,
//                            GL_COLOR_BUFFER_BIT, GL_NEAREST);





                glfwSwapBuffers(glfwWindow);
            }

            @Override
            public void run() {
                // Setup an error callback
                GLFWErrorCallback.createPrint(System.err).set();

                // Initialize GLFW
                if (!glfwInit()) {
                    throw new IllegalStateException("Unable to initialize GLFW.");
                }





                // Configure GLFW
                glfwDefaultWindowHints();
                glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
                long monitor=glfwGetPrimaryMonitor();
                final GLFWVidMode mode = glfwGetVideoMode(monitor);

                glfwWindowHint(GLFW_RED_BITS, mode.redBits());
                glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
                glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
                glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
                glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

                // Create the window
                if(!debugging) {
                    glfwWindow = glfwCreateWindow(mode.width(), mode.height(), this.title, monitor, NULL);
                }else {
                    glfwWindow = glfwCreateWindow(mode.width(), mode.height(), this.title,monitor, NULL);
                }
                if (glfwWindow == NULL) {
                    throw new IllegalStateException("Failed to create the GLFW window.");
                }




                glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
                glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
                glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
                glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
                glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
                    Window.setWidth(newWidth);
                    Window.setHeight(newHeight);
                });
                // Make the OpenGL context current
                glfwMakeContextCurrent(glfwWindow);

                // Enable v-sync
                glfwSwapInterval(1);

                // Make the window visible
                glfwShowWindow(glfwWindow);




                // Initialize the audio device

                String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
                audioDevice = alcOpenDevice(defaultDeviceName);

                int[] attributes = {0};
                audioContext = alcCreateContext(audioDevice, attributes);
                alcMakeContextCurrent(audioContext);

                ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
                ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

                assert alCapabilities.OpenAL10 : "Audio library not supported.";

                // This line is critical for LWJGL's interoperation with GLFW's
                // OpenGL context, or any context that is managed externally.
                // LWJGL detects the context that is current in the current thread,
                // creates the GLCapabilities instance and makes the OpenGL
                // bindings available for use.
                GL.createCapabilities();

                glEnable(GL_BLEND);
                glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

                framebuffer = new Framebuffer(3840, 2160);
                this.pickingTexture = new PickingTexture(3840, 2160);
                glViewport(0, 0, 3840, 2160);

                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 10;
                Random random = new Random();
                StringBuilder buffer = new StringBuilder(targetStringLength);
                for (int i = 0; i < targetStringLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    buffer.append((char) randomLimitedInt);
                }
                try {

                    File dir=new File("Leveltemps");
                    if(!dir.isDirectory()){
                        System.out.println("FUCK,this shit ain't a directory");
                    }
                    cleanTemps(dir);
                    leveltemp = File.createTempFile("level", ".tmp",dir);
                    //File tempFile = File.createTempFile("MyAppName-", ".tmp");
                    leveltemp.deleteOnExit();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture,leveltemp);
                imguiLayer.initImGui();
                groundShader= AssetPool.getShader("assets/shaders/GroundShader.glsl");
                defaultShader= AssetPool.getShader("assets/shaders/default.glsl");
                pickingShader= AssetPool.getShader("assets/shaders/pickingShader.glsl");
                invertedShader=AssetPool.getShader("assets/shaders/Inverted.glsl");
                BlackShader=AssetPool.getShader("assets/shaders/Blackcolor.glsl");



                Shaders.add(defaultShader);
                Shaders.add(BlackShader);
                Shaders.add(invertedShader);
                Shaders.add(pickingShader);
                Shaders.add(groundShader);
                Window.changeScene(new LevelEditorSceneInitializer(clientThread,requests,responses));


                Unit.init();
                while (!glfwWindowShouldClose(glfwWindow)) {

                    try {

                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################
//TODO (fer the color, bro)     DAT  MAIN DRAW AND EDITOR LOOP SHIT IS HERE BRUVV




                        starttime=UniTime.getExact();
                        fractionPassed=1-(lastPhysics-UniTime.getExact())/physicsStep;
                        renderGame();
                        KeyListener.endFrame();
                        MouseListener.endFrame();
                        if(start){
                            begin();
                        }
                        if(endPlay){
                            endPlay=false;
                            stop();
                        }
                        if(UniTime.getExact()-starttime<1/70f) {
                            java.util.concurrent.locks.LockSupport.parkNanos((long) ((1/70f-UniTime.getExact()+starttime)*nano));
                        }

                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################
                        //################################################################


                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

                // Destroy the audio context
                alcDestroyContext(audioContext);
                alcCloseDevice(audioDevice);

                // Free the memory
                glfwFreeCallbacks(glfwWindow);
                glfwDestroyWindow(glfwWindow);

                // Terminate GLFW and the free the error callback
                glfwTerminate();
                glfwSetErrorCallback(null).free();
            }
        });
        screen.start();



        while (!scened) {
            TimeUnit.MILLISECONDS.sleep(100);
        }



        while (screen.isAlive()) {



            //actual physics n shite bein done here

            if (runtimePlaying) {

                while (physicsTimes > 0) {
                    physicsTimes--;

                    currentScene.update(physicsStep, physicsTimes == 0);

                }


                endTime = UniTime.getExact();
                dt = endTime - beginTime;
                beginTime = endTime;
                while (lastPhysics <= endTime) {
                    lastPhysics += physicsStep;
                    UniTime.setFrame(lastPhysics);
                    physicsTimes += 1;
                }
                if (physicsTimes < 1) {

                    java.util.concurrent.locks.LockSupport.parkNanos((long) ((lastPhysics - endTime) * nano));
                    lastPhysics += physicsStep;
                    UniTime.setFrame(lastPhysics);
                    physicsTimes += 1;
                    beginTime = UniTime.getExact();

                }
            }
            if(end){
                end=false;
                endPlay=true;
            }

        }
    }
    public static void sendMove(Vector2f position,List<Integer> Ids){

        ClientData clientData = new ClientData();
        clientData.setName("Move");
        clientData.setGameObjects(Ids);
        List<Float> pos = new ArrayList<>();
        pos.add(position.get(0));
        pos.add(position.get(1));
        clientData.setPos(pos);
        get().requests.add(clientData);
    }
    public static void sendMove(Vector2f position,List<Integer> Ids,int targetId){

        ClientData clientData = new ClientData();
        clientData.setIntValue(targetId);
        clientData.setName("Move");
        clientData.setGameObjects(Ids);
        List<Float> pos = new ArrayList<>();
        pos.add(position.get(0));
        pos.add(position.get(1));
        clientData.setPos(pos);
        get().requests.add(clientData);
    }
//    public static void sendBuild(String name,List<Integer> Ids){
//
//        ClientData clientData = new ClientData();
//        clientData.setName("Build");
//        clientData.setGameObjects(Ids);
//        List<Float> pos = new ArrayList<>();
//        clientData.setPos(pos);
//        get().requests.add(clientData);
//    }
    public static void sendCast(List<Integer> Ids,int AbilityID){
        if(!KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            casting = false;
        }
        ClientData clientData = new ClientData();
        clientData.setGameObjects(Ids);
        clientData.setName("Cast");
        clientData.setIntValue(AbilityID);
        List<Float> pos= new ArrayList<Float>();
        pos.add(MouseListener.getWorld().x);
        pos.add(MouseListener.getWorld().y);
        clientData.setPos(pos);
        get().requests.add(clientData);
    }
    public static void targetCast(List<Integer> Ids,int AbilityID){
        targetIds=Ids;
        targetAbility=AbilityID;
        casting=true;
    }
    public static void activateCast(){

        sendCast(targetIds,targetAbility);
    }
    public static void cancelCast(){
        casting=false;
    }
//    public static void sendSelfcast(List<Integer> Ids,String AbilityName){
//
//        ClientData clientData = new ClientData();
//        clientData.setName(AbilityName);
//        clientData.setGameObjects(Ids);
//        get().requests.add(clientData);
//    }
    public static HashMap<Vector2i, Vector3i> getFloor(){
        return floor;
    }
    public static Boolean checkIfNewFloor(){
        if(newFloor){
          newFloor=false;
          return true;
        }
        return false;
    }
    public static void UpdateFloor(HashMap<Vector2i, Vector3i> flor){
        floor=flor;
        newFloor=true;
    }
    public static int getWidth() {
        return 3840;//get().width;
    }

    public static int getHeight() {
        return 2160;//get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static Framebuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImguiLayer() {
        return get().imguiLayer;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {

            case GameRequestPlay -> {
                if (!ready) {
                    ready = true;
                    ClientData request = new ClientData();
                    request.setName("start");
                    currentScene.save(null);
                    try {
                        request.setIntValue2(GameObject.ID_COUNTER);
                        request.setString(Files.readString(leveltemp.toPath(), StandardCharsets.UTF_8));
                    }catch (java.io.IOException e){
                        e.printStackTrace();
                    }
                    requests.add(request);

                }
            }
            case GameEngineStartPlay -> {
                if(!runtimePlaying) {
                    ready = true;
                    start=true;

                }else {
                    System.out.println("ALREADY RUNNIN YOU BOZO, what the f are you even doing???");
                }

            }
            case GameEngineStopPlay -> {
                    ready=false;
                    ClientData request = new ClientData();
                    request.setName("stop");
                    requests.add(request);
                    this.runtimePlaying = false;
                    end=true;


            }
            case Scan -> {
                List<GameObject> GoSel=getImguiLayer().getPropertiesWindow().getActiveGameObjects();
                List<GameObject> Go= currentScene.getGameObjects();
                HashSet<Vector4f> positions=new HashSet<>(Go.size());
                if (GoSel.isEmpty()) {
                    for (GameObject go : Go) {
                        if (!positions.add(new Vector4f(go.transform.position.x, go.transform.position.y, go.transform.scale.x, go.transform.scale.y))) {
                            go.destroy();
                        }

                    }
                }else{
                    for (GameObject og : GoSel) {
                        if (!positions.add(new Vector4f(og.transform.position.x, og.transform.position.y, og.transform.scale.x, og.transform.scale.y))) {
                            og.destroy();
                        }

                    }
                    for (GameObject go : Go) {
                        if(!GoSel.contains(go) && positions.contains(new Vector4f(go.transform.position.x, go.transform.position.y, go.transform.scale.x, go.transform.scale.y))){
                            go.destroy();
                        }
                    }
                }



            }
            case LoadLevel -> Window.changeScene(new LevelEditorSceneInitializer(clientThread,requests,responses));
            case SaveLevel -> currentScene.save(event.strargs);
            case ChangeLevel -> {
                ChangeLevel(event.strargs);
                Window.changeScene(new LevelEditorSceneInitializer(clientThread,requests,responses));
            }

        }
    }
}
