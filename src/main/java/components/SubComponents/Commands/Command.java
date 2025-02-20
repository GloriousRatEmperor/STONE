package components.SubComponents.Commands;

import components.SubComponents.SubComponent;
import components.unitcapabilities.Brain;
import jade.GameObject;

public class Command extends SubComponent {
    protected Brain brain;

    public void apply(GameObject self){

    }
    public void done(){
        brain.notifyDone();
    }
    public void setBrain(Brain brain){
        this.brain=brain;
    }
    public boolean cancel(){
        kill();
        return true;
    }
    public void update(float dt,GameObject self){

    }
    public void kill(){
        this.brain=null;
    }
}
