package jade;

import Multiplayer.ClientData;
import Multiplayer.ServerData;
import components.ServerInputs;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.joml.Vector2f;
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

import javax.swing.plaf.synth.Region;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    public Thread clientThread;
    public BlockingQueue<ClientData> requests;
    public BlockingQueue<ServerData> responses;
    private int width, height;
    private String title;
    private long glfwWindow;
    private File leveltemp;
    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlaying = false;
    private float beginTime;
    private float endTime;
    private float dt;
    boolean start=false;
    boolean ready=false;
    public Time time=new Time();

    private int physicsTimes;
    private float fractionPassed;
    private float physicsStep;
    private float lastPhysics;

    private static Window window = null;

    private long audioContext;
    private long audioDevice;

    private static Scene currentScene;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Jade";
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

    public void run() throws NoSuchFieldException {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

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
    private void cleanTemps(File dir){
        List<File> cleans =getFilesOF(dir.getAbsolutePath());
        for (File f:cleans){
            if(f.lastModified()<System.currentTimeMillis()-300000){
                if(!f.delete()){
                    System.out.println("ÜGH deletion failed");
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

        for(File temp : Objects.requireNonNull(directoryFile.listFiles())) {
                results.add(temp);
        }
        return results;
    }
    public void init() {
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
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        long monitor=glfwGetPrimaryMonitor();
        final GLFWVidMode mode = glfwGetVideoMode(monitor);
        // Create the window
        glfwWindow = glfwCreateWindow(mode.width(), mode.height(), this.title, monitor, NULL);
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

        this.framebuffer = new Framebuffer(3840, 2160);
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

        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture,leveltemp);
        this.imguiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInitializer(clientThread,requests,responses));
    }

    public void loop() throws NoSuchFieldException {
         beginTime = time.getTime();
         dt = -1.0f;
         physicsTimes=0;
         fractionPassed=0f;
         physicsStep=1/60f;
         lastPhysics=time.getTime();

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");


        while (!glfwWindowShouldClose(glfwWindow)) {

            // Poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 3840, 2160);
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2. Render actual game
            DebugDraw.beginFrame();

            this.framebuffer.bind();
            Vector4f clearColor = currentScene.camera().clearColor;
            glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT);



            //actual physics n non render shite bein done here
            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    //currentScene.update(dt,true);
                    while(physicsTimes>0) {
                        physicsTimes--;
                        currentScene.update(physicsStep, physicsTimes == 0);

                        KeyListener.endFrame();
                        MouseListener.endFrame();

                    }
                    currentScene.visualUpdate(fractionPassed);
                } else {
                    currentScene.editorUpdate(dt);
                    KeyListener.endFrame();
                    MouseListener.endFrame();
                }

            }



            currentScene.render();
            DebugDraw.draw();





            this.framebuffer.unbind();

            this.imguiLayer.update(currentScene,runtimePlaying);
            if(start){
                start=false;
                ServerInputs inputs= currentScene.getGameObject("LevelEditor").getComponent(ServerInputs.class);
                long StartTime=inputs.getStartTime();
                time.setBeginTime(StartTime);
                inputs.setTime(0f);
                beginTime=0f;
                lastPhysics = 0f;
                physicsTimes=0;


                currentScene.save(false);

                Window.changeScene(new LevelSceneInitializer(clientThread, requests, responses));
                ServerInputs newInputs= currentScene.getGameObject("LevelEditor").getComponent(ServerInputs.class);
                newInputs.setTime(0f);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
            while(lastPhysics<=endTime){
                lastPhysics+=physicsStep;
                physicsTimes+=1;
            }
            fractionPassed=1-(lastPhysics-endTime)/physicsStep;
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
    public static void sendSelfcast(List<Integer> Ids,String AbilityName){

        ClientData clientData = new ClientData();
        clientData.setName(AbilityName);
        clientData.setGameObjects(Ids);
        get().requests.add(clientData);
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
                    requests.add(request);

                }
            }
            case GameEngineStartPlay -> {
                if(!runtimePlaying) {
                    ready = true;
                    start=true;
                    this.runtimePlaying = true;
                }else {
                    System.out.println("ALREADY RUNNIN YOU BOZO, what the f are you even doing???");
                }

            }
            case GameEngineStopPlay -> {
                //if(ready) {
                    ready=false;
                    this.runtimePlaying = false;
                    Window.changeScene(new LevelEditorSceneInitializer(clientThread, requests, responses));
               // }
            }
            case LoadLevel -> Window.changeScene(new LevelEditorSceneInitializer(clientThread,requests,responses));
            case SaveLevel -> currentScene.save(true);
        }
    }
}
