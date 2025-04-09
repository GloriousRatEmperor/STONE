package components.gamestuff;

import components.Component;
import components.unitcapabilities.defaults.CastAbilities;
import enums.BotState;
import enums.Difficulty;
import jade.GameObject;
import jade.Window;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class PlayerBot extends Component {
    public int allied;
    private List<GameObject> gameObjects;

    public float updateInterval=1;
    private float updateClock=0;
    private BotState state=BotState.Agressive;


    public void setGameObjects(List<GameObject> gameObjectz){
        gameObjects=gameObjectz;
    }
    @Override
    public void start(){
        if(Window.runtimePlaying) {
            gameObject.removeComponent(this);
            Window.getScene().addPlayerBot(this);
        }
    }
    public void setDifficulty(Difficulty diff){
        switch(diff){
            case ReallyEasy ->
                updateInterval=4;
            case Nothard ->
                updateInterval=1;
            case Tryhard ->
                updateInterval=0;
        }


    }

    protected Vector3d money(){
        return Window.getScene().getmoney(allied);
    }


public List<List<Integer>> considerCasting(float dt) {//returns a list where the first is always the abilityID and after a list off all unit ids that want to cast that ability
    List<List<Integer>> casts = new ArrayList<>();
    for (GameObject go : gameObjects) {
        if (go.allied != allied) {
            continue;
        }
        CastAbilities cast = go.getComponent(CastAbilities.class);
        if (cast == null) {
            continue;
        }
        List<Integer> unitcasts = cast.consider();
        if (unitcasts.isEmpty()) {
            continue;
        }
        for (List<Integer> abilitIds : casts) {
            if (casts.contains(abilitIds.get(0))) {
                abilitIds.add(go.getUid());
                unitcasts.remove(abilitIds.get(0));
                if (unitcasts.isEmpty()) {
                    break;
                }
            }
        }
        for (Integer unitcast : unitcasts) {
            List<Integer> castPart = new ArrayList<>();
            castPart.add(unitcast);
            castPart.add(go.getUid());
            casts.add(castPart);
        }
    }
    return casts;


}

    public void cleanseCasts(List<List<Integer>> casts){ //this thing can now reject certain abilitycasts, such as build army unit if it is in expanding state
                                                                // or reject build base if it is agressive
        int len=casts.size();
        for(int i=0;i<len;i++){
            List<Integer> cast=casts.get(i);
            int abilityId=cast.get(0);
            if(abilityId>=100&&abilityId<900){
                casts.remove(cast);
                len--;
                i--;
            }
        }
    }
    public void sendCasts(List<List<Integer>> casts){
        for(List<Integer> cast:casts){
            int abilityId=cast.get(0);
            cast.remove(0);
            Window.sendCastNotarget(cast,abilityId);
        }
    }

    @Override
    public void update(float dt) {
        if (updateClock <= 0) {
            updateClock = updateInterval;
        } else {
            updateClock -= dt;
            return;
        }
        List<List<Integer>> casts=considerCasting(dt);
        cleanseCasts(casts);
        sendCasts(casts);
        
    }
}
