package components.unitcapabilities;

import components.unitcapabilities.damage.Mortal;
import jade.GameObject;

public class aggroDetector extends CircleSensor {

    public aggroDetector(float range){
        super(range);
    }
    public aggroDetector(){
        super();
        this.range=2.5f;

    }
    public void aggroTo(GameObject go){
        Brain brain=gameObject.getComponent(Brain.class);
        brain.aggro(go.transform);
        setActive(false);
    }
    @Override
    public void objectDetected(GameObject collidingObject) {
        if(collidingObject.getComponent(Mortal.class)!=null) {
            aggroTo(collidingObject);
        }
    }

}
