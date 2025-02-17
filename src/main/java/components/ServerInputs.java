package components;

import Multiplayer.ServerData;
import components.SubComponents.Commands.CastCommand;
import components.SubComponents.Commands.MoveCommand;
import components.unitcapabilities.Brain;
import jade.GameObject;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3i;
import util.UniTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ServerInputs extends Component {
    private BlockingQueue<ServerData> responses;
    private ArrayList<ServerData> responseList=new ArrayList<>();
    private float time=0;

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
                    if(response.getTime()<time-dt){
                        System.out.println("FUCK, got message really fucking late");
                        System.out.println("frames late: "+ ((time-response.getTime())/dt));
                    }
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

                ArrayList<GameObject> selectedObjects = Window.getScene().runningGetGameObjects(serverData.getGameObjects());
                List<Float> pos= serverData.getPos();
                GameObject target= Window.getScene().runningGetGameObject(serverData.getIntValue());
                boolean Qmoving =serverData.intValue2>=10;
                if(Qmoving){


                    serverData.intValue2-=10;
                }
                if(target!=null){
                    boolean stopAtTarget=false;
                    if(target.allied==selectedObjects.get(0).allied) {
                        stopAtTarget=true;
                    }

                        for (GameObject selectedObject : selectedObjects){
                            Brain brain=selectedObject.getComponent(Brain.class);
                            if(brain==null){
                                continue;
                            }
                            int tolerance;
                            if(!stopAtTarget) {
                                tolerance=-100;

                            }else {
                                tolerance = -1;
                            }
                            if(Qmoving){
                                brain.setGuard(false);
                            }

                            if(serverData.intValue2==1) {
                                brain.addCommand(new MoveCommand( target.transform,tolerance));
                            }else{
                                brain.setCommand(new MoveCommand( target.transform,tolerance));
                            }

                        }
                    }

                else {
                    for (GameObject selectedObject : selectedObjects) {

                        Brain brain=selectedObject.getComponent(Brain.class);
                        if(brain==null){
                            continue;
                        }
                        if(Qmoving){
                            brain.setGuard(false);
                        }
                        if(serverData.intValue2==1) {
                            brain.addCommand(new MoveCommand(new Vector2f(pos.get(0), pos.get(1))));
                        }else{
                            brain.setCommand(new MoveCommand(new Vector2f(pos.get(0), pos.get(1))));
                        }

                    }
                }
            }
            case "start" -> {
                EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
                UniTime.setStartNow();
                HashMap<Vector2i, Vector3i> newfloor= makeMap(serverData.getMap1(),serverData.getMap2(),serverData.getMap3());

                int space=serverData.getIntValue2();
                int count=serverData.getIntValue3();
                newfloor.put(new Vector2i(0,1),new Vector3i(count,space,0));
                Window.floor=newfloor;
                Window.startData=serverData;
            }
            case "Cast" -> {
                ArrayList<GameObject> selectedObjects = Window.getScene().runningGetGameObjects(serverData.getGameObjects());
                for (GameObject go:
                     selectedObjects) {
                    Brain brain=go.getComponent(Brain.class);
                    if(brain!=null){
                        if(serverData.intValue2==1) {
                            brain.addCommand(new CastCommand(serverData.intValue, serverData.position));
                        }else{
                            brain.setCommand(new CastCommand(serverData.intValue, serverData.position));
                        }
                    }
                }
            }
        }
    }
    public HashMap<Vector2i,Vector3i> makeMap(List<Integer> map1,List<Integer> map2,List<Integer> map3){
        HashMap<Vector2i, Vector3i> map= new HashMap<>();

        for (int e=0; e<map1.size(); e++){

            map.put(new Vector2i(map1.get(e),map2.get(e)),new Vector3i( map3.get(e*3),map3.get(e*3+1),map3.get(e*3+2)));
        }
        return map;
    }

    public void setTime(float tim){

        time=tim;
    }

}
