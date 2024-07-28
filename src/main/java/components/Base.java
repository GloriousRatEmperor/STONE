package components;

import jade.GameObject;
import jade.MineralCluster;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3i;
import util.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static jade.Window.*;

public class Base extends Component{
    private float range=30f;

    private float mineralDistance=2.1f;
    private boolean genned=false;
    private List<MineralCluster> ownedClusters=new ArrayList<MineralCluster>();




    public Base Clone(){
        Base base=new Base();
        return base;
    }
    @Override
    public void destroy(){
        getScene().removeBase(super.gameObject);
        GameObject nearest= getScene().getClosestBase(super.gameObject.transform.position);
        if(nearest!=null) {
            Base nearbase=nearest.getComponent(Base.class);
            ownedClusters.forEach(e -> e.owner = nearbase);
            nearbase.ownedClusters.addAll(ownedClusters);
        }
        ownedClusters.clear();

    }
    @Override
    public void update(float dt) {
        if (!this.genned){
            genMinerals();
            getScene().addBase(super.gameObject);
            genned=true;
        }
    }
    public void genMinerals(){
        MineralCluster cluster= new MineralCluster();
        cluster.owner=this;
        HashMap<Vector2i, Vector3i> floor=getFloor();
        //int count=floor.get(new Vector2i(0,1)).x;
        int spacing=floor.get(new Vector2i(0,1)).y;
        Vector2f position=super.gameObject.transform.position;
        //assuming there always zero zero point!

        int rang=((int) range/spacing)+1;
        Vector2i point= new Vector2i(((int) (position.x/spacing)-rang)*spacing, ((int) (position.y/spacing)-rang)*spacing);

        for (int i=0;i<rang*2+1;i++){
            for (int b=0;b<rang*2+1;b++){
                double distance=Math.sqrt( Math.pow( point.x+i*spacing-position.x,2)+Math.pow( point.y+b*spacing-position.y,2));
                if(distance<range){
                    int posx=point.x+i*spacing;
                    int posy=point.y+b*spacing;
                    Vector3i color=floor.get(new Vector2i(posx ,posy));
                    if(color!=null& !Objects.equals(color, new Vector3i(0, 0, 0))){}{
                        float minX=(float)((posx-position.x)/distance*mineralDistance+position.x);
                        float minY=(float)((posy-position.y)/distance*mineralDistance+position.y);
                        int biggest;

                        if(color.get(0)>color.get(1)){
                            if(color.get(0)>color.get(2)){
                                biggest=0;

                            }else{
                                biggest=2;
                            }
                        } else if (color.get(1)>color.get(2)) {
                            biggest=1;

                        }else{
                            biggest=2;
                        }


                        GameObject mineral=Unit.make("Mineral"+biggest, new Vector2f(minX, minY),1);
                        mineral.name="M";
                        Mineral miner=mineral.getComponent(Mineral.class);
                        miner.minerals=color;
                        miner.setCluster(cluster);
                        mineral.getComponent(SpriteRenderer.class).shaderIndex=1;
                        Window.getScene().addGameObjectToScene(mineral);
                        mineral.getComponent(SpriteRenderer.class).setColor(color.x/255f, color.y/255f, color.z/255f,1);
                        cluster.addMineral(mineral);
                        floor.replace(new Vector2i(posx ,posy), new Vector3i(0, 0, 0));
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
