package jade;

import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import renderer.DebugDraw;
import renderer.Renderer;
import renderer.Shader;
import util.AssetPool;

import java.io.File;
import java.io.IOException;

import static jade.Window.currentScene;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Drawer implements Runnable{
    private Window w;
    long glfwWindow;

    public Drawer(Window window) {
        this.w = window;
    }

    @Override
    public void run() {
        try {
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
            if(!w.debug) {
                glfwWindow = glfwCreateWindow(mode.width(), mode.height(), this.title, monitor, NULL);
            }else {
                glfwWindow = glfwCreateWindow(mode.width(), mode.height(), this.title, NULL, NULL);
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
            Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
            Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
            // This line is critical for LWJGL's interoperation with GLFW's
            // OpenGL context, or any context that is managed externally.
            // LWJGL detects the context that is current in the current thread,
            // creates the GLCapabilities instance and makes the OpenGL
            // bindings available for use.
            GL.createCapabilities();

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);


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


            int dt=0;
            while (!glfwWindowShouldClose(glfwWindow)) {
                synchronized (w) {
                    w.wait();
                }

                glfwPollEvents();

                // Render pass 1. Render to picking texture
                glDisable(GL_BLEND);
                w.pickingTexture.enableWriting();

                glViewport(0, 0, 3840, 2160);
                glClearColor(0, 0, 0, 0);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                Renderer.bindShader(pickingShader);
                currentScene.render();

                w.pickingTexture.disableWriting();
                glEnable(GL_BLEND);

                // Render pass 2. Render actual game
                DebugDraw.beginFrame();





                if(dt==1) {
                    Renderer.bindShader(defaultShader);
                }else {
                    dt=1;
                }
                w.getFramebuffer().bind();
                Vector4f clearColor = currentScene.camera().clearColor;
                glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
                glClear(GL_COLOR_BUFFER_BIT);


                if (w.runtimePlaying) {
                    currentScene.visualUpdate(w.fractionPassed);
                }
                currentScene.render();
                DebugDraw.draw();

                w.getFramebuffer().unbind();

                w.imguiLayer.update(currentScene,w.runtimePlaying);
                glfwSwapBuffers(w.glfwWindow);
            }
            // Free the memory
            glfwFreeCallbacks(glfwWindow);
            glfwDestroyWindow(glfwWindow);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
