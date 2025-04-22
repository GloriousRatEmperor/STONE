package components.unitcapabilities;

import components.Component;
import components.gamestuff.SpriteRenderer;
import jade.GameObject;
import jade.MineralCluster;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3i;
import util.Maf;
import util.UnitUtils.MiscCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static jade.MineralCluster.mineralClusters;
import static jade.Window.*;

public class Base extends Component {
    private float range=15f;
    private float mineralDistance=4f;
    private boolean genned=false;
    private static transient ThreadLocal<List<GameObject>> bases=new ThreadLocal<>(){
        @Override
        protected List<GameObject> initialValue()
        {
            return new ArrayList<>();
        }
    };
    private transient List<MineralCluster> ownedClusters=new ArrayList<MineralCluster>();

    public static void addBase(GameObject gameobject){
        Vector2f pos=gameobject.transform.position;
        bases.get().add(gameobject);
        Base base=gameobject.getComponent(Base.class);
        for(MineralCluster min:mineralClusters.get()){
            Base ownbase=min.getOwner(gameobject.allied);
            double dist=Maf.distance(pos, min.position);
            if(ownbase!=null) {
                GameObject own =ownbase.gameObject;
                if (dist <Maf.distance(own.transform.position,min.position)){
                    if(dist > base.mineralDistance/physicsStep*2){
                        min.setOwner(gameobject.allied,base);
                        base.ownedClusters.add(min);
                        ownbase.removeCluster(min);
                    }
                }
                continue;

            }
            if(dist> base.mineralDistance/physicsStep*2){
                min.setOwner(gameobject.allied,base);
                base.ownedClusters.add(min);
            }
        }

    }
    public static void removeBase(GameObject gameobject){
        bases.get().remove(gameobject);
    }
    public static void clearBases(){
        bases.get().clear();
    }
    public static GameObject getClosestBase(Vector2f position){
        GameObject base=null;
        double lastDistance=99999999;
        for (GameObject go:bases.get()){
            double  distance = Math.sqrt( Math.pow( go.transform.position.x-position.x,2)+Math.pow( go.transform.position.y-position.y,2));
            if (distance<lastDistance){
                lastDistance=distance;
                base=go;
            }
        }
        return base;

    }
    public static GameObject getClosestBase(Vector2f position, int allied){
        GameObject base=null;
        double lastDistance=99999999;
        for (GameObject go:bases.get()){
            if(go.allied!=allied){
                continue;
            }
            double  distance = Math.sqrt( Math.pow( go.transform.position.x-position.x,2)+Math.pow( go.transform.position.y-position.y,2));
            if (distance<lastDistance){
                lastDistance=distance;
                base=go;
            }
        }
        return base;

    }

    public MineralCluster assignMineral(){
        if(ownedClusters.isEmpty()){
            return null;
        }
        return ownedClusters.get(0);
    }
    public void removeCluster(MineralCluster mineralCluster){
        ownedClusters.remove(mineralCluster);
    }    public void addCluster(MineralCluster mineralCluster){
        ownedClusters.add(mineralCluster);
    }
    @Override
    public void destroy(){
        removeBase(super.gameObject);
        GameObject nearest= getClosestBase(super.gameObject.transform.position);
        if(nearest!=null) {
            Base nearbase=nearest.getComponent(Base.class);
            ownedClusters.forEach(e -> e.setOwner(gameObject.allied,nearbase));
            nearbase.ownedClusters.addAll(ownedClusters);
        }else{
            ownedClusters.forEach(e -> e.setOwner(gameObject.allied,null));
        }
        ownedClusters.clear();

    }
    @Override
    public void update(float dt) {

        if (!this.genned){
            if(0<=gameObject.allied&&gameObject.allied<=get().playerAmount+1) {
                genMinerals();
                addBase(super.gameObject);
                genned = true;
            }else{
                genned=true;
                System.out.println("prevented a base from working because high (2 or more over the player amount) " +
                        "or sub-zero allied bases aren't supported due to lack of a reason why they should be supported");
            }
        }
    }
    public void genMinerals(){
        Vector2f position=super.gameObject.transform.position;
        MineralCluster cluster= new MineralCluster(gameObject.allied,position);
        cluster.setOwner(gameObject.allied, this);
        HashMap<Vector2i, Vector3i> floor=getFloor();
        //int count=floor.get(new Vector2i(0,1)).x;
        int spacing=floor.get(new Vector2i(0,1)).y;
        //assuming there always zero zero point!

        int rang=((int) range/spacing)+1;
        Vector2i point= new Vector2i(((int) (position.x/spacing)-rang)*spacing, ((int) (position.y/spacing)-rang)*spacing);

        for (int i=0;i<rang*2+1;i++){
            for (int b=0;b<rang*2+1;b++){
                double distance=Math.sqrt( Math.pow( point.x+i*spacing-position.x,2)+Math.pow( point.y+b*spacing-position.y,2));
                if(distance<range){
                    int posx=point.x+i*spacing;
                    int posy=point.y+b*spacing;
                    Vector2i pos=new Vector2i(posx ,posy);
                    Vector3i color=floor.get(pos);
                    if(color!=null){
                        if (color.x + color.y + color.z != 0) {
                            float minX = (float) ((posx - position.x) / distance * mineralDistance + position.x);
                            float minY = (float) ((posy - position.y) / distance * mineralDistance + position.y);
                            int biggest;

                            if (color.get(0) > color.get(1)) {
                                if (color.get(0) > color.get(2)) {
                                    biggest = 0;

                                } else {
                                    biggest = 2;
                                }
                            } else if (color.get(1) > color.get(2)) {
                                biggest = 1;

                            } else {
                                biggest = 2;
                            }


                            GameObject mineral = MiscCreator.makeMisc("Mineral" + biggest, new Vector2f(minX, minY), 1);
                            mineral.name = "M";

                            Mineral miner = mineral.getComponent(Mineral.class);
                            mineral.getComponent(SpriteRenderer.class).setColor(color.x/255f, color.y/255f, color.z/255f, 1);
                            miner.minerals = color.mul(10);
                            miner.setOrigin(pos);
                            miner.setCluster(cluster);
                            mineral.getComponent(SpriteRenderer.class).shaderIndex = 1;
                            Window.getScene().addGameObjectToScene(mineral);

                            cluster.addMineral(mineral);
                            floor.replace(pos, new Vector3i(0, 0, 0));
                        }
                    }
                }
            }
        }
        if(!cluster.isEmpty()){
            ownedClusters.add(cluster);
        }
        UpdateFloor(floor);
    }
}
