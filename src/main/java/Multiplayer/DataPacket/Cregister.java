package Multiplayer.DataPacket;

public class Cregister extends ClientData {
    private boolean isBot;
    public void setBot(boolean val){
        isBot=val;
    }
    public boolean isBot(){
        return isBot;
    }
}
