package Multiplayer.DataPacket;

import java.util.List;

public class Scast extends ServerData {
    private int abilityID;
    private List<Float> position;
    private List<Integer> GameObjects;
    private boolean shiftCommand;
    private int target;
    public int getTarget(){
        return target;
    }
    public void setTarget(int newtarget){
        target=newtarget;
    }
    public List<Integer> getGameObjects() {
        return GameObjects;
    }
    public void setGameObjects(List<Integer> newval) {
        GameObjects=newval;
    }
    public void setAbilityID(int newid){
        abilityID=newid;
    }
    public int getAbilityID(){
        return abilityID;
    }
    public void setPos(List<Float> pos){position=pos;}
    public List<Float> getPos(){return position;}
    public void setShiftCommand(boolean val){
        shiftCommand=val;

    }
    public boolean isShiftCommand(){
        return shiftCommand;
    }
}
