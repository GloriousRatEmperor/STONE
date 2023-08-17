package components;

import Multiplayer.ClientData;
import Multiplayer.ServerData;
import jade.GameObject;
import jade.MouseListener;
import jade.Window;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class ServerInputs extends Component {
    private BlockingQueue<ServerData> responses;
    private Thread clientThread;
    public ServerInputs(Thread clientThread, BlockingQueue<ServerData> responses) {
        this.clientThread = clientThread;
        this.responses = responses;
    }
    @Override
    public void update(float dt) {;
        try {
            ServerData response;
            while (!responses.isEmpty()) {
                response = responses.take();
                apply(response);
            }
        } catch (InterruptedException e) {
        throw new RuntimeException(e);
        }
    }
    @Override
    public void editorUpdate(float dt) {

    }
    private void apply(ServerData serverData){
        if (Objects.equals(serverData.getName(), "Move")){
            ArrayList<GameObject> selectedObjects=Window.getScene().getGameObjects(serverData.getGameObjects());
            Window.getImguiLayer().getMenu().move(serverData.getPos().get(0),serverData.getPos().get(1), selectedObjects);
        }
    }



}
