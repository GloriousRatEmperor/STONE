package scenes;

import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.ServerData;
import components.gamestuff.*;
import components.unitcapabilities.defaults.Sprite;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import jade.GameObject;
import util.AssetPool;
import util.Img;

import java.util.concurrent.BlockingQueue;

import static jade.Window.get;
import static jade.Window.getScene;

public class LevelSceneInitializer extends SceneInitializer {
    private GameObject gamestuff;
    private BlockingQueue<ClientData> requests;
    private BlockingQueue<ServerData> responses;
    private Sprite bloodIcon;
    private Sprite magicIcon;
    private Sprite rockIcon;

    public LevelSceneInitializer(BlockingQueue<ClientData> requests,BlockingQueue<ServerData> responses) {
        this.requests=requests;
        this.responses=responses;
    }

    @Override
    public void init(Scene scene) {
        gamestuff = scene.createGameObject("LevelEditor");
        gamestuff.setNoSerialize();

        if(get().hasDrawThread) {
            Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

            gamestuff.addComponent(new MouseControls(requests));
            gamestuff.addComponent(new KeyControls(requests));
            gamestuff.addComponent(new GizmoSystem(gizmos)); //(whatever the f a gizmo is...) #NEW WARNING
            GameObject cameraObject = scene.createGameObject("GameCamera");
            cameraObject.addComponent(new GameCamera(scene.camera()));
            cameraObject.start();
            scene.addGameObjectToScene(cameraObject);
            bloodIcon= Img.get("blood");
            magicIcon=Img.get("magic");
            rockIcon=Img.get("rockicon");

        }
        gamestuff.addComponent(new ServerInputs(responses));
        scene.addGameObjectToScene(gamestuff);
        //added ends



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

        for (GameObject g : scene.getGameObjectsDraw()) {
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
        float moneySizeX=io.getDisplaySizeX()/8.5f;
        float moneySizeY=io.getDisplaySizeY()/6;
        ImGui.setNextWindowSize(moneySizeX,moneySizeY);
        ImGui.setNextWindowPos(0,40);
        ImGui.begin("moneyshower9000" , ImGuiWindowFlags.MenuBar| ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoTitleBar| ImGuiWindowFlags.NoCollapse| ImGuiWindowFlags.NoDecoration);

        if(ImGui.beginChild("Show",moneySizeX,moneySizeY,false, ImGuiWindowFlags.NoMouseInputs|ImGuiWindowFlags.NoTitleBar|ImGuiWindowFlags.NoDecoration)) {
            int iconSize=40;
            ImGui.image(bloodIcon.getTexture().getId(), iconSize, iconSize, bloodIcon.getTexCoords()[2].x, bloodIcon.getTexCoords()[0].y, bloodIcon.getTexCoords()[0].x, bloodIcon.getTexCoords()[2].y);
            ImGui.sameLine();
            ImGui.textWrapped("Blood "+ ((int) getScene().getBlood()));

            ImGui.image(rockIcon.getTexture().getId(), iconSize, iconSize, rockIcon.getTexCoords()[2].x, rockIcon.getTexCoords()[0].y, rockIcon.getTexCoords()[0].x, rockIcon.getTexCoords()[2].y);
            ImGui.sameLine();
            ImGui.textWrapped("Rocks "+((int)getScene().getRock()));

            ImGui.image(magicIcon.getTexture().getId(), iconSize, iconSize, magicIcon.getTexCoords()[2].x, magicIcon.getTexCoords()[0].y, magicIcon.getTexCoords()[0].x, magicIcon.getTexCoords()[2].y);
            ImGui.sameLine();
            ImGui.textWrapped("Magic "+ ((int)getScene().getMagic()));

        }ImGui.endChild();
        ImGui.end();
        ImGui.setNextWindowSize(moneySizeX,moneySizeY);
        ImGui.setNextWindowPos(io.getDisplaySizeX()-moneySizeX,40);
        ImGui.begin("moneyshowerover9000" , ImGuiWindowFlags.MenuBar| ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoTitleBar| ImGuiWindowFlags.NoCollapse| ImGuiWindowFlags.NoDecoration);
        if(ImGui.beginChild("Showenemy",moneySizeX,moneySizeY,false, ImGuiWindowFlags.NoMouseInputs|ImGuiWindowFlags.NoTitleBar|ImGuiWindowFlags.NoDecoration)) {


            ImGui.textWrapped("Blood "+ ((int) getScene().getEnemyBlood()));
            ImGui.textWrapped("Rocks "+((int)getScene().getEnemyRock()));
            ImGui.textWrapped("Magic "+ ((int)getScene().getEnemyMagic()));


        }ImGui.endChild();
        ImGui.end();




    }
}
