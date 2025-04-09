package Multiplayer.DataPacket;

import java.util.List;

public class Sstart extends ServerData{
    private int idCounter;
    private String levelSave;
    private int playerAmount;
    private int playerAllied;
    private long startTime;
    private int mineralSpacing;
    private int mineralCount;
    public List<Integer> xCoords;  // x1, x2 ...
    public List<Integer> yCoords;   // y1, y2 ...
    public List<Integer> colors; // r, g, b, r, g, b ...
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
    public void setPlayerAmount(int val){
        playerAmount=val;
    }
    public int getPlayerAmount(){
        return playerAmount;
    }
    public void setStartTime(long val){
        startTime=val;
    }
    public long getStartTime(){
        return startTime;
    }
    public void setPlayerAllied(int val){
        playerAllied=val;
    }
    public int getPlayerAllied(){
        return playerAllied;
    }
    public void setMineralSpacing(int val){
        mineralSpacing=val;
    }
    public int getMineralSpacing(){
        return mineralSpacing;
    }
    public void setMineralCount(int val){
        mineralCount=val;
    }
    public int getMineralCount(){
        return mineralCount;
    }

    public void setMap(List<Integer> mapp1,List<Integer> mapp2,List<Integer> mapp3){
        xCoords =mapp1;
        colors =mapp3;
        yCoords =mapp2;
    }
    public List<Integer> getxCoords(){
        return xCoords;
    }
    public List<Integer> getyCoords(){
        return yCoords;
    }
    public List<Integer> getColors(){
        return colors;
    }
}
