package Multiplayer.DataPacket;

public class Smessage extends ServerData{
    private String message;
    private int color;
    private String senderName;

    public void setMessage(String val){
        message=val;
    }
    public String getMessage(){
        return message;
    }
    public void setSenderName(String val){
        senderName=val;
    }
    public String getSenderName(){
        return senderName;
    }
    public void setColor(int val){
        color=val;
    }
    public int getColor(){
        return color;
    }

}
