package components.SubComponents.Effects;


import components.SubComponents.Commands.FearCommand;
import components.unitcapabilities.Brain;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;

public class Fear extends Effect {
    public Vector2f direction;
    public Fear(float duration, float power) {
        super(duration, power);
        this.sprite= Img.get("skull");
        type="curse";
        updateDesc();
    }


    @Override
    public void updateDesc(){
        setDesc("is running away in fear for "+Math.round(durationNow)+" seconds");
    }
    @Override
    public void apply(GameObject self){
        if(self.getComponent(Brain.class)!=null){
            Brain brain=self.getComponent(Brain.class);
            brain.priorityCommand(new FearCommand(direction));
            brain.stunTimer=Double.MAX_VALUE;
        }
    }
    @Override
    public void expire(GameObject self) {
        if(self.getComponent(Brain.class)!=null){
            Brain brain=self.getComponent(Brain.class);
            brain.stunTimer=0;
        }
    }

}
