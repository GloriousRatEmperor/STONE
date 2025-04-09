package Multiplayer.DataPacket;

public class ServerData extends Data{
    public float time;

    public void setTime(float tim){time=tim;} //uses special value -1 to signify as soon as possible for non-gametime dependent actions like sending messages
    public float getTime(){return time;}
}
