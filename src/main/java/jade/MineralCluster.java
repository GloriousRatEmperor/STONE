package jade;

import components.Base;

import java.util.ArrayList;
import java.util.List;

public class MineralCluster {
    public List<GameObject> minerals=new ArrayList<>();
    public Base owner;
    private int mineralCount=0;
    private int mineralCycle=0;
    public GameObject assignMineral(){
        if (!minerals.isEmpty()) {
            mineralCycle++;
            if (mineralCycle >= mineralCount) {
                mineralCycle = 0;

            }
            return minerals.get(mineralCycle);
        }return null;
    }
    public void addMineral(GameObject mineral){
        minerals.add(mineral);
        mineralCount++;
    }
    public void removeMineral(GameObject mineral){
        minerals.remove(mineral);
    }
    public Boolean isEmpty(){
        return minerals.isEmpty();
    }

}
