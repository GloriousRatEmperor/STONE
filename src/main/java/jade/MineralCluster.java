package jade;

import components.unitcapabilities.Base;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static components.unitcapabilities.Base.getClosestBase;
import static jade.Window.playerAmount;

public class MineralCluster {
    public transient List<GameObject> minerals=new ArrayList<>();
    private transient ArrayList<Base> owner;
    public Vector2f position;
    private int mineralCount=0;
    private int mineralCycle=0;





    public static List<MineralCluster> mineralClusters=new ArrayList();

    public static void addCluster(MineralCluster mineralCluster){
        mineralClusters.add(mineralCluster);
    }
    public static void removeCluster(MineralCluster mineralCluster){
        mineralClusters.remove(mineralCluster);
    }




    public MineralCluster(int creatorAllied, Vector2f position){
        owner=new ArrayList<>();
        mineralClusters.add(this);
        for (int i=0;i<playerAmount+2;i++){
            if(i==creatorAllied){
                owner.add(null);
                continue;
            }

            GameObject closest= getClosestBase(position,i);
            if(closest!=null) {
                Base base=closest.getComponent(Base.class);
                owner.add(base);
                base.addCluster(this);

            }else{
                owner.add(null);
            }
        }
        this.position=new Vector2f(position);

    }
    public Base getOwner(int allied){
        return owner.get(allied);
    }
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
    public void setOwner(int allied,Base base){
        owner.set(allied,base);
    }
    public Boolean removeMineral(GameObject mineral){//returns isEmpty
        if(minerals.contains(mineral)) {
            minerals.remove(mineral);
            mineralCount--;

            if (isEmpty()) {
                if(owner!=null){
                    for (Base b:owner
                         ) {
                        if (b != null) {
                            b.removeCluster(this);
                        }

                    }

                }
                owner=null;
                mineralClusters.remove(this);
                return true;
            } else {

                return false;
            }
        }return isEmpty();

    }
    public Boolean isEmpty(){
        return minerals.isEmpty();
    }

}
