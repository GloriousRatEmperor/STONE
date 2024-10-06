package Multiplayer;

import java.util.List;

public class ClientData {
    public int intValue;
    public int intValue2;
    public String name;
    public String string;
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
    public List<Integer> GameObjects;
    public List<Float> position;

    public List<Integer> getGameObjects() {
        return GameObjects;
    }
    public void setGameObjects(List<Integer> newval) {
        GameObjects=newval;
    }
    public void setPos(List<Float> pos){position=pos;}
    public List<Float> getPos(){return position;}

}
