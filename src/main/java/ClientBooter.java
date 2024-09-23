import Multiplayer.ClientData;
import Multiplayer.ServerData;
import Multiplayer.TechnicalClient;
import jade.Window;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientBooter {


    public static void main(String[] args) throws Exception {
//        Transform t =new Transform();
//        Vector2f b=new Vector2f();
//        b=t.position;
//        t.position.set(2,5);
//        System.out.println("it is "+b.x);
//        System.exit(0);
//
        //CastAbilities c=new CastAbilities();
//        BuildBase a=(BuildBase) c.getAbility(AbilityName.BuildBase);
//        a.setRace(4);
//        c.addAbility(a);
//        GameObject o=new GameObject("s");
//        o.addComponent(c);
//        System.exit(0);



//        float xAdd = 0.5f;
//        float yAdd = 0.5f;
//        for (int i=0; i < 4; i++) {
//
//            if (i == 1) {
//                yAdd = -0.5f;
//            } else if (i == 2) {
//                xAdd = -0.5f;
//            } else if (i == 3) {
//                yAdd = 0.5f;
//            }
//            System.out.println(xAdd);
//            System.out.println(yAdd);
//        }






//        File dir=new File("Leveltemps");
//        File leveltemp = File.createTempFile("level", ".tmp",dir);
//        FileUtil.copyFile("permalevel.txt",leveltemp.getPath(),true);
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//
//                .registerTypeAdapter(Component.class, new ComponentDeserializer())
//                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
//                .enableComplexMapKeySerialization()
//                .create();
//
//        String inFile = "";
//        try {
//
//            inFile = new String(Files.readAllBytes(Paths.get(leveltemp.getPath())));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (!inFile.equals("")) {
//            int maxGoId = -1;
//            int maxCompId = -1;
//            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
//            for (int i = 0; i < objs.length; i++) {
//                GameObject object=objs[i];
//                    System.out.println(object.allied);
//
//
//            }
//        }
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