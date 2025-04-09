package Multiplayer.DataPacket;

import java.util.List;

public class Cmove extends ClientData {
    private int targetID;
    private List<Integer> GameObjects;
    private boolean shiftCommand;
    private List<Float> position;
    private boolean qmove;
    public void setQmove(boolean val){
        qmove=val;

    }
    public boolean isQmove(){
        return qmove;
    }
    public List<Integer> getGameObjects() {
        return GameObjects;
    }
    public void setGameObjects(List<Integer> newval) {
        GameObjects=newval;
    }
    public void setPos(List<Float> pos){position=pos;}
    public List<Float> getPos(){return position;}
    public int getTargetID(){
        return targetID;
    }
    public void setTargetID(int newval){
        targetID=newval;
    }
    public void setShiftCommand(boolean val){
        shiftCommand=val;

    }
    public boolean isShiftCommand(){
        return shiftCommand;
    }
}
