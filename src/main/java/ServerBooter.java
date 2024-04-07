import Multiplayer.TechnicalServer;
import jade.Window;

import java.io.File;
import java.io.IOException;

public class ServerBooter {
    public static void main(String[] args) throws Exception {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        new TechnicalServer(port,1).run();
    }
}
