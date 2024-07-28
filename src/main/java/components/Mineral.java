package components;

import jade.MineralCluster;
import org.joml.Vector3i;

public class Mineral extends Component{
    @Override
    public Mineral Clone(){
        return new Mineral();
    }
    public Vector3i minerals;
    private MineralCluster cluster;
    public Base getTiedBase(){
        return cluster.owner;
    }
    @Override
    public void destroy(){
        deplete();
    }
    public void deplete(){
        cluster.minerals.remove(super.gameObject);
    }
    public Mineral(){
    }
    public void setCluster(MineralCluster cluster){
        this.cluster = cluster;
    }
    public MineralCluster getCluster(){
        return cluster;
    }
    public Mineral(MineralCluster cluster){
        this.cluster = cluster;
    }
}
