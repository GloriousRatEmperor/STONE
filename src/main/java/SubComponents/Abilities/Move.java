package SubComponents.Abilities;

import Multiplayer.ServerData;
import jade.GameObject;
import components.MoveContollable;
import util.Img;

public class Move extends Ability{
    @Override
    public Move Copy(){
        Move Move=new Move(id);
        return Move;
    }
    public Move( int id) {
        super(id);
        mp=0;
        sprite = Img.get("move");
        targetable=true;
        setDesc( "issues a move command");
    }
    public void cast(ServerData data, GameObject self) {
        MoveContollable move=self.getComponent(MoveContollable.class);
        if (move!=null) {
            move.moveCommand(data.position, self);
        }
    }
}
