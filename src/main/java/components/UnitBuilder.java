package components;

import jade.GameObject;
import jade.Transform;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Unit;

import java.util.ArrayList;

public class UnitBuilder extends Component{
    public ArrayList<UNMADE> queue= new ArrayList<>();
    public int alliedB =1;
    public transient Transform tr;
    private int time=0;
    @Override
    public UnitBuilder Clone(){
        return new UnitBuilder();
    }
    @Override
    public void begin(){
//        super.isactive=true;
//        if(this.gameObject.getComponent(CastAbilities.class)==null){
//            this.gameObject.addComponent(new CastAbilities());
//        };
//        CastAbilities ability=this.gameObject.getComponent(CastAbilities.class);
//        builder=ability.getAbility("Build unit");
//        ability.addAbility(builder);
        alliedB=super.gameObject.allied;

    }
    public void start() {
        this.tr = gameObject.getComponent(Transform.class);
    }
    @Override
    public void update(float dt){
        time+=dt;
        if(!queue.isEmpty()){
            UNMADE unm=queue.get(0);
            if(unm.time<time){
                makeUnit(unm);
            }
        }


    }

    public void makeUnit(UNMADE unm) {
        Vector2f position = new Vector2f(tr.position);
        GameObject unit = Unit.make(unm.name,position, alliedB);
        Window.getScene().addGameObjectToScene(unit);
    }
    public void addqueue(String UnitName, Vector3f cost){
        UNMADE unm=new UNMADE(UnitName);
        unm.time+=time;
        queue.add(unm);
        Window.addmoney(-cost.x, -cost.y, -cost.z);
        queue.sort((s1, s2) -> (int) (s1.time-s2.time) );
    }




}
