package components.gamestuff;

import Multiplayer.DataPacket.*;
import components.Component;
import components.SubComponents.Commands.CastCommand;
import components.unitcapabilities.Brain;
import editor.Menu;
import jade.GameObject;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
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

    public ServerInputs(BlockingQueue<ServerData> responses) {
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
                        if(response.getTime()!=-1){
                            System.out.println("FUCK, got message really fucking late");
                            System.out.println("frames late: "+ ((time-response.getTime())/dt));
                        }

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
    private void apply(ServerData data){
        Class<? extends ServerData> dataClass=data.getClass();
        if (dataClass.equals(Smove.class)) {
            Smove serverData = (Smove) data;
            ArrayList<GameObject> selectedObjects = Window.getScene().runningGetGameObjects(serverData.getGameObjects());
            List<Float> pos = serverData.getPos();
            GameObject target = null;
            if (serverData.getTargetID() != -1) {
                target = Window.getScene().runningGetGameObject(serverData.getTargetID());
                if (target == null) {
                    return;
                }
            }
            if (selectedObjects.isEmpty()) {
                return;
            }
            boolean Qmoving = serverData.isQmove();

            if (target != null) {
                boolean stopAtTarget = false;
                if (target.allied == selectedObjects.get(0).allied) {
                    stopAtTarget = true;
                }

                for (GameObject selectedObject : selectedObjects) {
                    Brain brain = selectedObject.getComponent(Brain.class);
                    if (brain == null) {
                        continue;
                    }
                    int tolerance;
                    if (!stopAtTarget) {
                        tolerance = -100;

                    } else {
                        tolerance = -1;
                    }
                    if (Qmoving) {
                        brain.setGuard(false);
                    }

                    if (serverData.isShiftCommand()) {

                        brain.addCommand(brain.moveCommand(target.transform, tolerance, Qmoving));
                    } else {
                        brain.setCommand(brain.moveCommand(target.transform, tolerance, Qmoving));
                    }

                }
            } else {
                for (GameObject selectedObject : selectedObjects) {

                    Brain brain = selectedObject.getComponent(Brain.class);
                    if (brain == null) {
                        continue;
                    }
                    if (Qmoving) {
                        brain.setGuard(false);
                    }
                    if (serverData.isShiftCommand()) {
                        brain.addCommand(brain.moveCommand(new Vector2f(pos.get(0), pos.get(1)), Qmoving));
                    } else {
                        brain.setCommand(brain.moveCommand(new Vector2f(pos.get(0), pos.get(1)), Qmoving));
                    }

                }
            }
        } else if (dataClass.equals(Sstart.class)) {
            Sstart serverData = (Sstart) data;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
            HashMap<Vector2i, Vector3i> newfloor = makeMap(serverData.getxCoords(), serverData.getyCoords(), serverData.getColors());

            int space = serverData.getMineralSpacing();
            int count = serverData.getMineralCount();
            newfloor.put(new Vector2i(0, 1), new Vector3i(count, space, 0));
            get().floor = newfloor;
            get().startData = serverData;
        } else if (dataClass.equals(Scast.class)) {
            Scast serverData = (Scast) data;
            ArrayList<GameObject> selectedObjects = Window.getScene().runningGetGameObjects(serverData.getGameObjects());
            for (GameObject go :
                    selectedObjects) {
                Brain brain = go.getComponent(Brain.class);
                if (brain != null) {
                    if (serverData.isShiftCommand()) {
                        brain.addCommand(new CastCommand(serverData.getAbilityID(), serverData.getPos(), serverData.getTarget()));
                    } else {
                        brain.setCommand(new CastCommand(serverData.getAbilityID(), serverData.getPos(), serverData.getTarget()));
                    }
                }
            }
        } else if (dataClass.equals(Smessage.class)) {
            Smessage serverData = (Smessage) data;
            Menu.addMessage(new Message(serverData.getSenderName(), serverData.getMessage(), serverData.getColor()));
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
