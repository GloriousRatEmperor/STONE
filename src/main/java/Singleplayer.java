//being made


import Multiplayer.*;

import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.ServerData;
import jade.Window;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Singleplayer {


    public static void main(String[] args) throws Exception {


        Boolean debugging= Boolean.valueOf(args[0]);


        int port = 8080;
        Thread serverThread= new Thread(new TechnicalServer(port,1));
        serverThread.start();

        BlockingQueue<ClientData> requests=new ArrayBlockingQueue<>(15);
        BlockingQueue<ServerData> responses=new ArrayBlockingQueue<>(500);
        Thread.UncaughtExceptionHandler h = (th, ex) -> System.out.println("Uncaught exception with server: " + ex);
        String adress="localhost";

        Thread clientThread= new Thread(new TechnicalClient(adress,requests,responses));
        clientThread.setUncaughtExceptionHandler(h);
        clientThread.start();


        Window window = Window.get();
        window.clientThread=clientThread;
        window.requests=requests;
        window.responses=responses;
        window.run(debugging);




    }
}