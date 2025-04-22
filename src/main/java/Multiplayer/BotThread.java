package Multiplayer;

import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.ServerData;
import jade.Window;

import java.util.concurrent.BlockingQueue;

public class BotThread implements Runnable{
    private BlockingQueue<ClientData> requests;
    private BlockingQueue<ServerData> responses;
    public BotThread( BlockingQueue<ClientData> requests, BlockingQueue<ServerData> responses){
        this.requests=requests;
        this.responses=responses;

    }

    @Override
    public void run() {

        try {
            Window window = Window.get();
            window.clientThread = null;
            window.requests = requests;
            window.responses = responses;
            window.run(false);
        }catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
