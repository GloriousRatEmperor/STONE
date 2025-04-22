package components.unitcapabilities;

import components.Component;
import components.SubComponents.Commands.MoveCommand;
import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import jade.MineralCluster;
import jade.Transform;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3d;
import physics2d.components.Rigidbody2D;

import static components.unitcapabilities.Base.getClosestBase;
import static jade.Window.get;
import static jade.Window.getScene;

public class Worker extends Component {
    public double harvestAmount=5;
    public MineralCluster harvestCluster;
    public Mineral currentMineral;
    private Boolean conserveMineral=false;
    private Vector3d cargo=new Vector3d(); //blood stone magic
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
            if(gameObject.allied==target.allied) {
                depositMineral(base);
            }
        }
    }
    public void harvest(Mineral mineral){
        
        int allblood=mineral.minerals.x, allstone=mineral.minerals.y, allmagic=mineral.minerals.z;
        int total=allblood+allstone+allmagic;
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
                return;
            }
        }
        Base homeBase=harvestCluster.getOwner(gameObject.allied);
        if(homeBase!=null){
            GameObject base=homeBase.gameObject;
            MoveContollable move=super.gameObject.getComponent(MoveContollable.class);

            if(move!=null) {
                conserveMineral=true;
                Brain brain=gameObject.getComponent(Brain.class);
                brain.addCommand(new MoveCommand(base.transform));
            }else{
                System.out.println("your worker has no movecontrollable that's kinda weird");
            }
        }


    }

    public void depositMineral(Base base){
        getScene().addmoney(cargo,gameObject.allied);
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
    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (cargo.x != 0 | cargo.y != 0 | cargo.z != 0) {
            Rigidbody2D body = super.gameObject.getComponent(Rigidbody2D.class);
            body.addVelocity(new Vector2f(contactNormal).perpendicular().mul(1.0f));
        }
    }
    public void moveToMineral(){

        Brain brain=gameObject.getComponent(Brain.class);
        GameObject mineral;
        if(currentMineral==null) {
            mineral=harvestCluster.assignMineral();
            currentMineral = mineral.getComponent(Mineral.class);
        }else{
            mineral=currentMineral.gameObject;
        }
        if (brain != null) {
            conserveMineral=true;
            brain.addCommand(new MoveCommand(mineral.transform));

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
    @Override
    public void start() {
        if(get().runtimePlaying) {
            Brain brain=gameObject.getComponent(Brain.class);
            GameObject nearbase=getClosestBase(gameObject.transform.position);
            if(nearbase==null){
                return;
            }
            brain.addCommand(new MoveCommand(nearbase.transform));
            this.gameObject.getComponent(Rigidbody2D.class).friction = 0;
        }
    }


}
