

import Multiplayer.ClientData;
import Multiplayer.ServerData;

import Multiplayer.TechnicalClient;
import jade.Window;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientBooter {

    public static void main(String[] args) throws Exception {
//        Float e=0f;
//        while(e<10000){
//            e+=1/5f;
//        }
//        System.out.println(e);
//        System.exit(0);
        BlockingQueue<ClientData> requests=new ArrayBlockingQueue<>(15);
        BlockingQueue<ServerData> responses=new ArrayBlockingQueue<>(150);
        Thread.UncaughtExceptionHandler h = (th, ex) -> System.out.println("Uncaught exception: " + ex);
        String adress=args[0];

        Thread clientThread= new Thread(new TechnicalClient(adress,requests,responses));
        clientThread.setUncaughtExceptionHandler(h);
        clientThread.start();


        Window window = Window.get();
        window.clientThread=clientThread;
        window.requests=requests;
        window.responses=responses;
        window.run();




    }
}