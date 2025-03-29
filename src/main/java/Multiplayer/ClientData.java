package Multiplayer;

import java.util.List;

public class ClientData {
    private int intValue;
    private int target;
    private int intValue2;
    private String name;
    private String string;
    private List<Integer> GameObjects;
    private List<Float> position;
    public int getTarget(){
        return target;
    }
    public void setTarget(int newtarget){
        target=newtarget;
    }
    public int getIntValue(){
        return intValue;
    }
    public int getIntValue2(){
        return intValue2;
    }
    public String getName(){
        return name;
    }
    public void setIntValue(int newval){
        intValue=newval;
    }
    public void setIntValue2(int newval){
        intValue2=newval;
    }
    public void setName(String newval){ name =newval;}
    public void setString(String value){
        string=value;
    }
    public String getString(){
        return string;
    }
    public List<Integer> getGameObjects() {
        return GameObjects;
    }
    public void setGameObjects(List<Integer> newval) {
        GameObjects=newval;
    }
    public void setPos(List<Float> pos){position=pos;}
    public List<Float> getPos(){return position;}

}
