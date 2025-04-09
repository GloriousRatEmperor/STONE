package Multiplayer.DataPacket;

public class Cmessage extends ClientData {

    private String message;
    private int color;

    public void setMessage(String val){
        message=val;
    }
    public String getMessage(){
        return message;
    }
    public void setColor(int val){
        color=val;
    }
    public int getColor(){
        return color;
    }

}
