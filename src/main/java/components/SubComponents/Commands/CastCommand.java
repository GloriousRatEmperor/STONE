package components.SubComponents.Commands;

import components.unitcapabilities.defaults.CastAbilities;
import jade.GameObject;
import org.joml.Vector2f;
import util.Maf;

import java.util.List;

public class CastCommand extends Command{
    public int castID;
    public final Vector2f pos;
    public int castRange;
    public CastCommand(int castID,Vector2f pos){

        this.castID=castID;
        this.pos=new Vector2f(pos);

    }
    public CastCommand(int castID,List<Float> p){
        this(castID,new Vector2f(p.get(0),p.get(1)));

    }


    @Override
    public void apply(GameObject self) {
        CastAbilities cast=self.getComponent(CastAbilities.class);
        float range=cast.getAbility(castID).range;
        if(range==-1||Maf.distance(self.transform.position,pos)<=range) {
            cast.castAbility(castID, pos);
            done();
        }else{
            brain.priorityCommand(this);
            brain.priorityCommand(new MoveCommand(pos,castRange));
            done();
        }
    }


}
