package components.gamestuff;

public class Message {
    public String sender;
    public String msg;
    public int color=util.Img.color(0,0,0,255);
    public Message(String sender,String msg,int color){
        this.sender=sender;
        this.color=color;
        this.msg=msg;
    }
}
