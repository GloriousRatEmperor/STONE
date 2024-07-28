package components;

import jade.GameObject;
import jade.MineralCluster;
import org.joml.Vector3d;
import physics2d.components.MoveContollable;

import static jade.Window.getScene;

public class Worker extends Component{
    public double harvestAmount=5;
    public MineralCluster harvestCluster;
    public Mineral currentMineral;
    private Vector3d cargo=new Vector3d(); //blood stone magic
    @Override
    public Worker Clone(){
        Worker c=new Worker();
        return c;
    }
    public Worker(){

    }
    public Worker(double harvestAmount){
        this.harvestAmount=harvestAmount;
    }
    @Override
    public void Interact(GameObject target){
        Mineral mineral=target.getComponent(Mineral.class);
        Base base=target.getComponent(Base.class);


        if(mineral!=null){
            if(mineral!=currentMineral){
                currentMineral=mineral;
                harvestCluster=mineral.getCluster();
            }
            harvest(mineral);
        } else if (base!=null){
            depositMineral(base);
        }
    }
    public void harvest(Mineral mineral){
        
        int allblood=mineral.minerals.x, allstone=mineral.minerals.y, allmagic=mineral.minerals.z;
        int total=allblood+allstone+allmagic;
        cargo.x=harvestAmount*allblood/total;
        cargo.y=harvestAmount*allstone/total;
        cargo.z=harvestAmount*allmagic/total;



        if(harvestCluster.owner!=null){
            GameObject base=harvestCluster.owner.gameObject;
            MoveContollable move=super.gameObject.getComponent(MoveContollable.class);

            if(move!=null) {
                move.moveCommand(base.transform);
            }else{
                System.out.println("your worker has no movecontrollable that's kinda weird");
            }
        }


    }

    public void depositMineral(Base base){
        getScene().addmoney(cargo);
        cargo.negate();//setzero
        if(harvestCluster!=null) {
            MoveContollable move = super.gameObject.getComponent(MoveContollable.class);
            GameObject mineral = harvestCluster.assignMineral();

            if (move != null & mineral != null) {
                move.moveCommand(mineral.transform);
            }
        }

    }


}
