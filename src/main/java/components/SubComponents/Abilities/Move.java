package components.SubComponents.Abilities;

import components.SubComponents.Commands.MoveCommand;
import components.unitcapabilities.Brain;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;

public class Move extends Ability{
    @Override
    public Move Copy(){
        Move Move=new Move(type);
        return Move;
    }
    public Move(AbilityName type) {
        super(type);
        mp=0;
        sprite = Img.get("move");
        targetable=true;
    }

    public boolean cast(final Vector2f pos, GameObject self,GameObject target) {
        Brain brain=self.getComponent(Brain.class);
        if (brain!=null) {
            brain.addCommand(new MoveCommand(pos));
            return true;
        }else{
            return false;
        }
    }
}
