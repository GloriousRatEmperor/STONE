package scenes;

import Multiplayer.ClientData;
import Multiplayer.ServerData;
import components.*;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import jade.GameObject;
import util.AssetPool;

import java.util.concurrent.BlockingQueue;

import static jade.Window.getScene;

public class LevelSceneInitializer extends SceneInitializer {
    private GameObject gamestuff;

    private Thread clientThread;
    private BlockingQueue<ClientData> requests;
    private BlockingQueue<ServerData> responses;

    public LevelSceneInitializer(Thread clientThread,BlockingQueue<ClientData> requests,BlockingQueue<ServerData> responses) {
        this.clientThread=clientThread;
        this.requests=requests;
        this.responses=responses;
    }

    @Override
    public void init(Scene scene) {
        //added this
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

        gamestuff = scene.createGameObject("LevelEditor");
        gamestuff.setNoSerialize();
        gamestuff.addComponent(new MouseControls(clientThread,requests));//working
        gamestuff.addComponent(new KeyControls(clientThread,requests));
        //gamestuff.addComponent(new GridLines()); (normally adds grid... kina obvious tbh)
        gamestuff.addComponent(new GizmoSystem(gizmos)); //(whatever the f a gizmo is...) #NEW WARNING
        gamestuff.addComponent(new ServerInputs(clientThread,responses));
        scene.addGameObjectToScene(gamestuff);
        //added ends

        GameObject cameraObject = scene.createGameObject("GameCamera");
        cameraObject.addComponent(new GameCamera(scene.camera()));
        cameraObject.start();
        scene.addGameObjectToScene(cameraObject);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");


        AssetPool.addSpritesheet("assets/images/abilities.png",
                new Spritesheet(AssetPool.getTexture("assets/images/abilities.png"),
                        80, 80, 3, 0));

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.addSpritesheet("assets/images/turtle.png",
                new Spritesheet(AssetPool.getTexture("assets/images/turtle.png"),
                        16, 24, 4, 0));
        AssetPool.addSpritesheet("assets/images/bigSpritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/bigSpritesheet.png"),
                        16, 32, 42, 0));
        AssetPool.addSpritesheet("assets/images/pipes.png",
                new Spritesheet(AssetPool.getTexture("assets/images/pipes.png"),
                        32, 32, 4, 0));
        AssetPool.addSpritesheet("assets/images/items.png",
                new Spritesheet(AssetPool.getTexture("assets/images/items.png"),
                        16, 16, 43, 0));
        AssetPool.addSpritesheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");

        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);

        AssetPool.getSound(("assets/sounds/main-theme-overworld.ogg")).play();

        for (GameObject g : scene.getGameObjects()) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            if (g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imgui() {
        imgui.ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowSize(io.getDisplaySizeX()/8,io.getDisplaySizeY()/7);
        ImGui.setNextWindowPos(0,100);
        ImGui.begin("moneyshower9000" , ImGuiWindowFlags.MenuBar| ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoTitleBar| ImGuiWindowFlags.NoCollapse| ImGuiWindowFlags.NoDecoration);

        if(ImGui.beginChild("Show",600,700,false, ImGuiWindowFlags.NoMouseInputs|ImGuiWindowFlags.NoTitleBar|ImGuiWindowFlags.NoDecoration)) {


            ImGui.textWrapped("Blood "+ ((int) getScene().blood));
            ImGui.textWrapped("Rocks "+((int)getScene().rock));
            ImGui.textWrapped("Magic "+ ((int)getScene().magic));


        }ImGui.endChild();
        ImGui.end();




    }
}
