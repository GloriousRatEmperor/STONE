package scenes;

import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.ServerData;
import components.gamestuff.ServerInputs;
import components.gamestuff.*;
import components.unitcapabilities.defaults.Sprite;
import imgui.ImGui;
import imgui.ImVec2;
import jade.GameObject;
import jade.Prefabs;
import jade.Sound;
import org.joml.Vector2f;
import org.joml.Vector4i;
import physics2d.components.Box2DCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;
import util.AssetPool;
import util.Img;
import util.UnitUtils.BuildingCreator;
import util.UnitUtils.UnitCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import static jade.Window.get;
import static util.UnitUtils.BuildingCreator.buildNames;
import static util.UnitUtils.UnitCreator.unitNames;

public class LevelEditorSceneInitializer extends SceneInitializer {

    private Spritesheet sprites;
    private List<Sprite> extraSprites;
    private int placeAllied=1;


    private GameObject levelEditorStuff;
    private BlockingQueue<ClientData> requests;
    private BlockingQueue<ServerData> responses;


    public void setAllied(int val){
        placeAllied=val;
    }
    public LevelEditorSceneInitializer(BlockingQueue<ClientData> requests,BlockingQueue<ServerData> responses) {

        this.requests=requests;
        this.responses=responses;

    }

    @Override
    public void init(Scene scene) {
        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        if(get().hasDrawThread){
            sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
            extraSprites=AssetPool.getMapSheet("assets/images/spritesheets/joined.png").getSprites();
            Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");
            levelEditorStuff.addComponent(new MouseControls(requests));
            levelEditorStuff.addComponent(new KeyControls(requests));
            levelEditorStuff.addComponent(new GridLines());
            levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
            levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        }



        levelEditorStuff.addComponent(new ServerInputs(responses));


        scene.addGameObjectToScene(levelEditorStuff);
    }

    @Override
    public void loadResources(Scene scene) {

        HashMap<String, Vector4i> map1=new HashMap<>();
        try {
            File myObj = new File("assets/images/key.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] lit=data.split(";");
                map1.put(lit[4],new Vector4i(Integer.parseInt(lit[0]),Integer.parseInt(lit[1]),Integer.parseInt(lit[2]),Integer.parseInt(lit[3])));

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        AssetPool.addMapSpritesheet("assets/images/spritesheets/joined.png",
                new MapSpriteSheet(AssetPool.getTexture("assets/images/spritesheets/joined.png"),map1));


        AssetPool.addSpritesheet("assets/images/spritesheets/back.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/back.png"),
                        500, 500, 100, 0));

        AssetPool.addSpritesheet("assets/images/spritesheets/background.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/background.png"),
                        500, 500, 1, 0));






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

        AssetPool.getSound(("assets/sounds/main-theme-overworld.ogg")).stop();

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

        ImGui.setNextWindowSize(io.getDisplaySizeX()/4,io.getDisplaySizeY());
        ImGui.setNextWindowPos(io.getDisplaySizeX()*3/4,0);

        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.EditorStuffImgui();
        ImGui.end();

        ImGui.setNextWindowSize(io.getDisplaySizeX(),io.getDisplaySizeY()/5);
        ImGui.setNextWindowPos(0,io.getDisplaySizeY()/5*4);

        ImGui.begin("Item window");

        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Dead Blocks")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size()+extraSprites.size(); i++) {
                    Sprite sprite;

                    if(i>=sprites.size()){
                        sprite=extraSprites.get(i- sprites.size());
                    }else{
                        sprite = sprites.getSprite(i);
                    }
                    float spriteWidth = 16 * 4;
                    float spriteHeight = 16 * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        Rigidbody2D rb = new Rigidbody2D();
                        rb.setBodyType(BodyType.Static);
                        object.addComponent(rb);
                        Box2DCollider b2d = new Box2DCollider();
                        b2d.setHalfSize(0.25f, 0.25f);
                        object.addComponent(b2d);
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if ( nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Units")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < unitNames.size(); i++) {
                    String name= unitNames.get(i);
                    Sprite sprite = Img.get(name);

                    float spriteWidth = 64;
                    float spriteHeight = 64;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = UnitCreator.makeUnit(name,new Vector2f(),placeAllied);
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Buildings")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < buildNames.size(); i++) {
                    String name= buildNames.get(i);

                    Sprite sprite = Img.get(name);
                    if(sprite==null){
                        continue;
                    }

                    float spriteWidth = 64;
                    float spriteHeight = 64;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = BuildingCreator.makeBuilding(name,new Vector2f(),placeAllied);
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getFilepath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying()) {
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }

                    if (ImGui.getContentRegionAvailX() > 100) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        ImGui.end();
    }
}
