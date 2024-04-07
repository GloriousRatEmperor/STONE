package components;

import Multiplayer.ServerData;
import jade.GameObject;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static jade.Window.get;

public class ServerInputs extends Component {
    private BlockingQueue<ServerData> responses;
    private ArrayList<ServerData> responseList=new ArrayList<>();
    private float time=0;
    private int ally;
    public int space;
    public int count;
    private long startTime=0;
    private Thread clientThread;
    public ServerInputs(Thread clientThread, BlockingQueue<ServerData> responses) {
        this.clientThread = clientThread;
        this.responses = responses;
    }
    @Override
    public void update(float dt) {

        time+=dt;
        try {
            while (!responses.isEmpty()) {
                ServerData response = responses.take();
                responseList.add(response);
                responseList.sort((s1, s2) -> (int) (s1.getTime()-s2.getTime()) );

            }
            while(!responseList.isEmpty()){
                ServerData response=responseList.get(0);
                if(responseList.get(0).getTime()<time){
                    apply(response);
                    responseList.remove(response);
                }else{
                    break;
                }
            }
        } catch (InterruptedException e) {
        throw new RuntimeException(e);
        }
    }

    @Override
    public void editorUpdateDraw() {

        try {
            while (!responses.isEmpty()) {

                apply(responses.take());

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void apply(ServerData serverData){
        switch (serverData.getName()) {
            case "Move" -> {
                ArrayList<GameObject> selectedObjects = Window.getScene().getGameObjects(serverData.getGameObjects());
                Window.getImguiLayer().getMenu().move(serverData.getPos().get(0), serverData.getPos().get(1), selectedObjects);
            }
            case "start" -> {
                EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
                startTime = serverData.getStart();
                setTime(startTime);
                this.ally=serverData.getIntValue();


                get().floor=makeMap(serverData.getMap1(),serverData.getMap2(),serverData.getMap3());
                space=serverData.getIntValue2();
                count=serverData.getIntValue3();
            }
            case "Cast" -> {
                ArrayList<GameObject> selectedObjects = Window.getScene().getGameObjects(serverData.getGameObjects());
                for (GameObject go:
                     selectedObjects) {
                    CastAbilities cast=go.getComponent(CastAbilities.class);
                    if(cast!=null){
                        cast.castAbility(serverData);
                    }

                }
            }
            case "Speed" -> {
                List<Integer> obj=serverData.getGameObjects();
                ArrayList<GameObject> selectedObjects = Window.getScene().getGameObjects(obj);
                for (GameObject go:
                        selectedObjects) {
                    CastAbilities cast=go.getComponent(CastAbilities.class);
                    if(cast!=null){
                        cast.castAbility(serverData);
                    }else {
                        obj.remove(go.getUid());
                    }

                }
                serverData.setName("slow");
                serverData.setTime(time+5000);
                responseList.add(serverData);
            }

        }
    }
    public int getAlly(){
        return ally;
    }
    public HashMap<Vector2i,Vector3i> makeMap(List<Integer> map1,List<Integer> map2,List<Integer> map3){
        HashMap<Vector2i, Vector3i> map= new HashMap<>();

        for (int e=0; e<map1.size(); e++){

            map.put(new Vector2i(map1.get(e),map2.get(e)),new Vector3i( map3.get(e*3),map3.get(e*3+1),map3.get(e*3+2)));
        }
        return map;
    }
    public long getStartTime(){
        return startTime;
    }

    public void setTime(float tim){

        time=tim;
    }

}
