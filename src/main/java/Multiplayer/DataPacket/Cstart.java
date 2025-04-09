package Multiplayer.DataPacket;

public class Cstart extends ClientData {
    private int idCounter;
    private String levelSave;
    public void setLevelSave(String value){
        levelSave=value;
    }
    public String getLevelSave(){
        return levelSave;
    }
    public void setIdCounter(int val){
        idCounter=val;
    }
    public int getIdCounter(){
        return idCounter;
    }

}
