package components;

import jade.GameObject;
import jade.MineralCluster;
import jade.Transform;
import org.joml.Vector3d;
import components.MoveContollable;

import static jade.Window.getScene;

public class Worker extends Component{
    public double harvestAmount=5;
    public MineralCluster harvestCluster;
    public Mineral currentMineral;
    private Boolean conserveMineral=false;
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
        Base homeBase=harvestCluster.owner;
        if(total>harvestAmount) {
            cargo.x = harvestAmount * allblood / total;
            mineral.minerals.x-=harvestAmount * allblood / total;
            cargo.y = harvestAmount * allstone / total;
            mineral.minerals.y-=harvestAmount * allstone / total;
            cargo.z = harvestAmount * allmagic / total;
            mineral.minerals.z-=harvestAmount * allmagic / total;

        }else{
            cargo.x=allblood;
            cargo.y =allstone;
            cargo.z =allmagic;
            mineral.minerals.zero();
            if(!harvestCluster.removeMineral(mineral.gameObject)){
                currentMineral= harvestCluster.assignMineral().getComponent(Mineral.class);
            }else{
                this.harvestCluster=null;
                this.currentMineral=null;
            }
        }

        if(homeBase!=null){
            GameObject base=homeBase.gameObject;
            MoveContollable move=super.gameObject.getComponent(MoveContollable.class);

            if(move!=null) {
                conserveMineral=true;
                move.moveCommand(base.transform);
            }else{
                System.out.println("your worker has no movecontrollable that's kinda weird");
            }
        }


    }

    public void depositMineral(Base base){
        getScene().addmoney(cargo);
        cargo.zero();
        if(harvestCluster==null) {
            this.harvestCluster=base.assignMineral();
            if(harvestCluster!=null) {
                moveToMineral();
            }
        }else {
            moveToMineral();
        }



    }
    public void moveToMineral(){

        MoveContollable move = super.gameObject.getComponent(MoveContollable.class);
        GameObject mineral;
        if(currentMineral==null) {
            mineral=harvestCluster.assignMineral();
            currentMineral = mineral.getComponent(Mineral.class);
        }else{
            mineral=currentMineral.gameObject;
        }
        if (move != null) {
            conserveMineral=true;
            move.moveCommand(mineral.transform);

        }
    }
    @Override
    public void startMove(Transform target){//prevents workers from eternally remembering which mineral they are harvesting even if you issue move commands and shite...
        // TODO maybe should be handled diff
        if(!conserveMineral){
            currentMineral=null;
            harvestCluster=null;

        }
        conserveMineral=false;
    }


}
