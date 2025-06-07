import Multiplayer.TechnicalServer;

public class ServerBooter {
    public static void main(String[] args) throws Exception {

        int port = args.length > 2 ? Integer.parseInt(args[1]) : 8080;
        int playerCount= Integer.valueOf(args[0]);
        new TechnicalServer(port,playerCount).run();

    }
}
awaws\