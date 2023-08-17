import Multiplayer.TechnicalServer;
import jade.Window;

public class ServerBooter {
    public static void main(String[] args) throws Exception {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        new TechnicalServer(port).run();
        Window window = Window.get();
        window.run();
    }
}
