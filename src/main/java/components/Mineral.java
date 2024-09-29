package components;

import jade.MineralCluster;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class Mineral extends Component{
    @Override
    public Mineral Clone(){
        return new Mineral();
    }
    public Vector3i minerals;
    private MineralCluster cluster;
    private Vector2i origin;
    public Base getTiedBase(int allied){
        return cluster.getOwner(allied);
    }
    @Override
    public void destroy(){
        deplete();
    }
    public void deplete(){
        cluster.removeMineral(super.gameObject);
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
    public void setOrigin(Vector2i origin){
        this.origin=origin;
    }
}
