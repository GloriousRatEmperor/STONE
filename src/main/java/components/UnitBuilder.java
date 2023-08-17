package components;

import jade.GameObject;
import jade.Prefabs;
import jade.Transform;
import jade.Window;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;

public class UnitBuilder extends Component{
    public float nextTime;
    public float cooldown=100;
    public float selectedUnit;
    public float queue;
    public int Allied =1;
    public transient Transform tr;
    @Override
    public UnitBuilder Clone(){
        return new UnitBuilder();
    }
    public void start() {
        this.tr = gameObject.getComponent(Transform.class);
        this.nextTime=cooldown;

    }
    @Override
    public void update(float dt){
        nextTime-=dt;
        if(nextTime<0){
            makeUnit();
            nextTime=cooldown;
        }

    }

    public void makeUnit(){
        Vector2f position = new Vector2f(tr.position);
        GameObject unit = Prefabs.generateUnit(position,Allied);
        Window.getScene().addGameObjectToScene(unit);
    }





}
