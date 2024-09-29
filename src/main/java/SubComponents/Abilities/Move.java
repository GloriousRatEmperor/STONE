package SubComponents.Abilities;

import Multiplayer.ServerData;
import components.MoveContollable;
import enums.AbilityName;
import jade.GameObject;
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

    public void cast(ServerData data, GameObject self) {
        MoveContollable move=self.getComponent(MoveContollable.class);
        if (move!=null) {
            move.moveCommand(data.position, self);
        }
    }
}
