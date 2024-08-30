package Multiplayer;

import java.util.List;

public class ServerData {
    public int intValue;
    public String strValue;
    public int intValue2;
    public int intValue3;
    public float time;
    public long start;
    public int playerAmount;
    public String name;
    public List<Integer> map1;
    public List<Integer> map2;
    public List<Integer> map3;
    public List<Integer> GameObjects;
    public List<Float> position;

    public void setPlayerAmount(int amount){
        playerAmount=amount;
    }
    public int getPlayerAmount(){
        return playerAmount;
    }
    public int getIntValue(){
        return intValue;
    }
    public String getstrValue(){
        return strValue;
    }
    public void setstrValue(String str){
        strValue=str;
    }
    public int getIntValue2(){
        return intValue2;
    }
    public int getIntValue3(){
        return intValue3;
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
    public void setIntValue3(int newval){
        intValue3=newval;
    }
    public void setStart(long newval){
        start=newval;
    }
    public void setName(String newval){ name =newval;}

    public void setMap(List<Integer> mapp1,List<Integer> mapp2,List<Integer> mapp3){
        map1=mapp1;
        map3=mapp3;
        map2=mapp2;
    }
    public List<Integer> getMap1(){
        return map1;
    }
    public List<Integer> getMap2(){
        return map2;
    }
    public List<Integer> getMap3(){
        return map3;
    }
    public List<Integer> getGameObjects() {
        return GameObjects;
    }
    public void setGameObjects(List<Integer> newval) {
        GameObjects=newval;
    }
    public void setPos(List<Float> pos){position=pos;}
    public List<Float> getPos(){return position;}
    public void setTime(float tim){time=tim;}
    public float getTime(){return time;}
    public Long getStart(){return start;}
}
