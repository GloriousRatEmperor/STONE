package components;

import Multiplayer.ClientData;
import Multiplayer.Server;
import Multiplayer.ServerData;
import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

import java.util.*;
import java.util.concurrent.BlockingQueue;

public class ServerInputs extends Component {
    private BlockingQueue<ServerData> responses;
    private ArrayList<ServerData> responseList=new ArrayList<>();
    private float time=0;
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
    private ArrayList<ServerData> sortedIndex(ArrayList<ServerData> array,ServerData value) {
        int low = 0;
        int high = array.size();

        while (low < high) {
            var mid = (low + high) >>> 1;
            if (array.get(mid).getTime() < value.getTime()){
                low = mid + 1;
            }
            else {
                high = mid;
            }
        }
        array.add(low,value);
        return array;
    }
    @Override
    public void editorUpdate(float dt) {
        time += dt;
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
            }
        }
    }
    public long getStartTime(){
        return startTime;
    }
    public void setTime(float tim){
        time=tim;
    }

}
