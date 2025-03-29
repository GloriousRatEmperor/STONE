package components.SubComponents.Abilities;

import components.unitcapabilities.Brain;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import util.Img;

public class GuardMode extends Ability{

    @Override
    public GuardMode Copy(){
        GuardMode guard=new GuardMode(type);
        return guard;
    }
    public GuardMode(AbilityName type) {
        super(type);
        sprite = Img.get("shield");


    }
    @Override
    public void updateDesc(){
        setDesc("toggles guard mode, preventing units from chasing after nearby enemies, Q-move to remove");
    }

    @Override
    public boolean cast(final Vector2f pos, GameObject self,GameObject target) {
        self.getComponent(Brain.class).setGuard(true);
        return true;
    }
}
