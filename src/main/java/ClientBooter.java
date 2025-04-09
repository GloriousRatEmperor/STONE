import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.ServerData;
import Multiplayer.TechnicalClient;
import jade.Window;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientBooter {


    public static void main(String[] args) throws Exception {

//        System.exit(0);



        Boolean debugging= Boolean.valueOf(args[1]);
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
    public static double calcDeviation(int maxRolls,int dice){
        double deviation=0;
        for(int e=0;e<dice;e++){
            deviation+=Math.pow( e+1-(maxRolls*2+1),2);
        }
        deviation=Math.sqrt( deviation/dice);

        return deviation;
    }
}