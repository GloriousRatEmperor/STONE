package util.UnitUtils;

import components.SubComponents.Animation.Animation;
import components.gamestuff.MapSpriteSheet;
import components.gamestuff.StateMachine;
import components.unitcapabilities.Brain;
import components.unitcapabilities.NonPickable;
import components.unitcapabilities.aggroDetector;
import components.unitcapabilities.defaults.Effects;
import components.unitcapabilities.defaults.Sprite;
import jade.GameObject;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;
import util.AssetPool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static jade.Prefabs.generateSpriteObject;
import static jade.Window.getScene;

public class CreatorTools {

    private static MapSpriteSheet items;

    public static void initStats(String statsPath, HashMap<String,Float> intoStats, ArrayList<String> names){
        File directoryPath = new File(statsPath);
        File[] fileList = directoryPath.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            try {

                File file = fileList[i];
                String fileName=file.getName().toLowerCase().split("\\.")[0];
                names.add(fileName);
                List<String> inFile = Files.readAllLines(Paths.get(file.getPath()));
                for (String s:inFile){
                    String[] oneLine=s.split(":");
                    for (int h=0;h<oneLine.length; h++){
                        oneLine[h]=oneLine[h].replaceAll(" ", "").toLowerCase();

                    }
                    if(oneLine.length==2){
                        if(oneLine[0].startsWith("unlock")){
                            UnlockTracker.unlockCosts.put(fileName+"."+oneLine[0].substring(6), Float.parseFloat(oneLine[1]) );
                            continue;
                        }
                        intoStats.put(fileName+"."+oneLine[0], Float.parseFloat(oneLine[1]) );
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    static GameObject genBase(String name, Vector2f position, int allied){

        GameObject newObject = generateSpriteObject(getSprite(name), 0.18f, 0.18f,name,position);
        newObject.allied=allied;
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        newObject.addComponent(rb);
        newObject.addComponent(new Brain());
        newObject.addComponent(new aggroDetector());

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        newObject.addComponent(circleCollider);
        newObject.addComponent(new Effects());
        return newObject;
    }
    public static Sprite getSprite(String name){
        if(items==null){
            items=AssetPool.getMapSheet("assets/images/spritesheets/joined.png");
        }
        return items.getSprite(name);
    }
    public static void generateAnimation(Vector2f position, float sizeX, float sizeY, Animation animation) {
        GameObject anime = generateSpriteObject(animation.getFirstSprite(), sizeX, sizeY, "disposableTemp", position);
        StateMachine stateMachine = new StateMachine();
        animation.title = "Animation";
        animation.setLoop(false);
        animation.setDeathOnCompletion(true);
        anime.addComponent(stateMachine);
        stateMachine.addState(animation);
        stateMachine.addState(animation.title, animation.title, animation.title);
        stateMachine.setDefaultState(animation.title);
        stateMachine.trigger(animation.title);
        anime.setNoSerialize();
        anime.addComponent(new NonPickable());
        anime.start();
        getScene().addDrawObjecttoScene(anime);
    }

}
