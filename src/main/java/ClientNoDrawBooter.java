import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.ServerData;
import Multiplayer.TechnicalClient;
import jade.Window;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientNoDrawBooter {

    public static void main(String[] args) throws Exception {

//        System.exit(0);



        Boolean debugging= false;
        BlockingQueue<ClientData> requests=new ArrayBlockingQueue<>(15);
        BlockingQueue<ServerData> responses=new ArrayBlockingQueue<>(150);
        Thread.UncaughtExceptionHandler h = (th, ex) -> System.out.println("Uncaught exception: " + ex);
        String adress=args[0];

        Thread clientThread= new Thread(new TechnicalClient(adress,requests,responses));
        clientThread.setUncaughtExceptionHandler(h);
        clientThread.start();


        Window window = Window.get();
        //window.clientThread=clientThread;
        window.requests=requests;
        window.responses=responses;
        window.run(debugging);




    }
}
