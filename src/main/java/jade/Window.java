package jade;

import Multiplayer.DataPacket.*;
import components.gamestuff.ServerInputs;
import components.unitcapabilities.Base;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector4f;
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
import util.UnitUtils.BuildingCreator;
import util.UnitUtils.MiscCreator;
import util.UnitUtils.ProjectileCreator;
import util.UnitUtils.UnitCreator;
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
    public boolean hasDrawThread=true;
    public Sstart startData;
    public BlockingQueue<ClientData> requests;
    public BlockingQueue<ServerData> responses;
    private int width, height;

    private File leveltemp;
    public int allied=0;
    private boolean end=false;
    public int playerAmount;
    private boolean endPlay=false;
    public ImGuiLayer imguiLayer;

    public Framebuffer framebuffer;
    public HashMap<Vector2i, Vector3i> floor;


    public boolean runtimePlaying = false;
    private float beginTime;
    private float endTime;


    public static ArrayList<Shader> Shaders = new ArrayList<Shader>();
    public boolean newFloor=false;

    private float dt;
    private final int nano=1000000000;
    boolean start=false;
    boolean ready=false;

    private int physicsTimes;
    private float fractionPassed;
    public static float physicsStep;
    private float lastPhysics;
    private float starttime;
    private static ThreadLocal<Window> window = new ThreadLocal<>();

    private long audioContext;
    private long audioDevice;
    public boolean scened=false;

    private Scene currentScene;
    public Boolean debugging;
    public static void setAllied(int allied){
        get().allied=allied;
    }

    private Window() {

        this.width = 1920;
        this.height = 1080;
        EventSystem.addObserver(this);

    }
    public static void changeScene(SceneInitializer sceneInitializer) {
        Scene current=get().currentScene;
        if (current != null) {
            current.destroy();
        }

        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        get().currentScene = new Scene(sceneInitializer, get().leveltemp);
        current=get().currentScene;
        current.load();
        current.init();
        current.start();
        get().scened=true;

    }
    public static void ChangeLevel(String lvlname){
        FileUtil.copyFile("ZlevelSaves/"+lvlname,get().leveltemp.getPath(),true);

    }

    public static Window get() {
        if (Window.window.get() == null) {
            Window.window.set( new Window());
        }

        return Window.window.get();
    }

    public static Physics2D getPhysics() { return get().currentScene.getPhysics(); }

    public static Scene getScene() {
        return get().currentScene;
    }

    public void run(Boolean debugging) throws NoSuchFieldException, InterruptedException {
        this.debugging=debugging;

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
    public void stop(){
        MineralCluster.mineralClusters.get().clear();
        Base.clearBases();
        glLineWidth(2.0f);

        Window.changeScene(new LevelEditorSceneInitializer( requests, responses));

    }

    public void begin() {
        this.runtimePlaying = true;
        ServerInputs inputs= currentScene.getGameObject("LevelEditor").getComponent(ServerInputs.class);

        UniTime.setStartNow();
        inputs.setTime(0f);
        beginTime=0;
        lastPhysics = 0;
        endTime=0;
        UniTime.setFrame(lastPhysics);
        physicsTimes=0;

        allied= startData.getPlayerAllied();
        getImguiLayer().getMenu().allied=allied;
        GameObject.setCounter(startData.getIdCounter());
        try {
            FileWriter writer = new FileWriter(leveltemp);
            String startLevel=startData.getLevelSave();
            writer.write(startLevel);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        currentScene.load();


        Window.changeScene(new LevelSceneInitializer( requests, responses));

        ServerInputs newInputs= currentScene.getGameObject("LevelEditor").getComponent(ServerInputs.class);

        Scene scene=Window.getScene();
        scene.setFloor(floor);
        scene.initiatePlayers(startData.getPlayerAmount());
        this.playerAmount=startData.getPlayerAmount();
        scene.setAllied(allied);
        newInputs.setTime(0f);
        Variables.start();

        starttime=UniTime.getExact();

        startData=null;
        glLineWidth(4.5f);
    }



    public void loop() throws NoSuchFieldException, InterruptedException {
         beginTime =0;
         dt = -1.0f;
         physicsTimes=0;
         fractionPassed=0f;
         physicsStep=1/60f;
         lastPhysics=0;
        Thread screen=null;
        Window runningWindow=window.get();
         if(hasDrawThread) {


             screen = new Thread(new Runnable() {

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


                     pickingTexture.disableWriting();
                     glEnable(GL_BLEND);

                     // Render pass 2. Render actual game
                     DebugDraw.beginFrame();
                     imguiLayer.update(currentScene, runtimePlaying);
                     framebuffer.bind();


                     if (runtimePlaying) {
                         Renderer.bindShader(groundShader);
                         currentScene.renderFloor();
                     } else {
                         Vector4f clearColor = currentScene.camera().clearColor;
                         glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
                         glClear(GL_COLOR_BUFFER_BIT);
                     }

                     Renderer.bindShader(defaultShader);
                     if (runtimePlaying) {

                         currentScene.visualUpdate(fractionPassed, runtimePlaying);
                     } else {
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

                     Window.window.set(runningWindow);

                     // Initialize GLFW
                     if (!glfwInit()) {
                         throw new IllegalStateException("Unable to initialize GLFW.");
                     }


                     // Configure GLFW
                     glfwDefaultWindowHints();
                     glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
                     glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
                     long monitor = glfwGetPrimaryMonitor();
                     final GLFWVidMode mode = glfwGetVideoMode(monitor);

                     glfwWindowHint(GLFW_RED_BITS, mode.redBits());
                     glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
                     glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
                     glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
                     glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

                     // Create the window
                     if (!debugging) {
                         glfwWindow = glfwCreateWindow(mode.width(), mode.height(), this.title, monitor, NULL);
                     } else {
                         glfwWindow = glfwCreateWindow(mode.width(), mode.height(), this.title, monitor, NULL);
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

                         File dir = new File("Leveltemps");
                         if (!dir.isDirectory()) {
                             new File("Leveltemps").mkdirs();
                         }
                         cleanTemps(dir);
                         leveltemp = File.createTempFile("level", ".tmp", dir);
                         //File tempFile = File.createTempFile("MyAppName-", ".tmp");
                         leveltemp.deleteOnExit();

                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }

                     imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture, leveltemp);
                     imguiLayer.initImGui();
                     groundShader = AssetPool.getShader("assets/shaders/GroundShader.glsl");
                     defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
                     pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
                     invertedShader = AssetPool.getShader("assets/shaders/Inverted.glsl");
                     BlackShader = AssetPool.getShader("assets/shaders/Blackcolor.glsl");


                     Shaders.add(defaultShader);
                     Shaders.add(BlackShader);
                     Shaders.add(invertedShader);
                     Shaders.add(pickingShader);
                     Shaders.add(groundShader);
                     Window.changeScene(new LevelEditorSceneInitializer(requests, responses));


                     BuildingCreator.init();
                     UnitCreator.init();
                     MiscCreator.init();
                     ProjectileCreator.init();
                     while (!glfwWindowShouldClose(glfwWindow)) {

                         try {

                             //################################################################
                             //################################################################
                             //################################################################
                             //################################################################
                             //################################################################
                             //################################################################
                             //################################################################
                            //TODO (not actual TODO it's fer the color, bro)     DAT  MAIN DRAW AND EDITOR LOOP SHIT IS HERE BRUVV


                             starttime = UniTime.getExact();
                             fractionPassed = 1 - (lastPhysics - UniTime.getExact()) / physicsStep;
                             renderGame();
                             KeyListener.endFrame();
                             MouseListener.endFrame();
                             if (start) {
                                 begin();
                                 start = false;
                             }
                             if (endPlay) {
                                 endPlay = false;
                                 stop();
                             }
                             if (UniTime.getExact() - starttime < 1 / 70f) {
                                 java.util.concurrent.locks.LockSupport.parkNanos((long) ((1 / 70f - UniTime.getExact() + starttime) * nano));
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
         }


        while (!scened) {
            TimeUnit.MILLISECONDS.sleep(100);
        }


        boolean first=true;
        while (!hasDrawThread||screen.isAlive()) {
            //actual physics n shite bein done here
            if (runtimePlaying&&!start) {
                if(first){
                    first=false;
                    util.UniTime.setStartNow();
                }
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
                if(end){
                    first=true;
                    end=false;
                    endPlay=true;
                    while(endPlay){
                        java.util.concurrent.locks.LockSupport.parkNanos(100);
                    }
                }
            }else if(!hasDrawThread&&start){
                begin();
                start = false;
            }

        }
    }
    public static HashMap<Vector2i, Vector3i> getFloor(){
        return Window.get().floor;
    }
    public static Boolean checkIfNewFloor(){
        Window wind=Window.get();
        if(wind.newFloor){
          wind.newFloor=false;
          return true;
        }
        return false;
    }
    public static void UpdateFloor(HashMap<Vector2i, Vector3i> flor){
        Window wind=Window.get();
        wind.floor=flor;
        wind.newFloor=true;
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
                    Cstart request = new Cstart();
                    if(currentScene!=null) {
                        currentScene.save(null);

                        request.setIdCounter(GameObject.ID_COUNTER.get());
                        try {
                            request.setLevelSave(Files.readString(leveltemp.toPath(), StandardCharsets.UTF_8));
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        request.setIdCounter(0);
                        request.setLevelSave("");
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
                    Cstop request = new Cstop();
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
            case LoadLevel -> Window.changeScene(new LevelEditorSceneInitializer(requests,responses));
            case SaveLevel -> currentScene.save(event.strargs);
            case ChangeLevel -> {
                ChangeLevel(event.strargs);
                Window.changeScene(new LevelEditorSceneInitializer(requests,responses));
            }

        }
    }
}
