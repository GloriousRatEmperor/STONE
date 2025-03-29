package components.SubComponents.Commands;

import components.SubComponents.Abilities.Ability;
import components.unitcapabilities.defaults.CastAbilities;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2f;
import util.Maf;

import java.util.List;

public class CastCommand extends Command{
    public int castID;
    public Vector2f pos;
    public int targetId=-1;
    public CastCommand(int castID,Vector2f pos,int targetId){

        this.castID=castID;
        this.pos=new Vector2f(pos);
        this.targetId=targetId;

    }
    public CastCommand(int castID,List<Float> p,int targetId){
        this(castID,new Vector2f(p.get(0),p.get(1)),targetId);

    }


    @Override
    public void apply(GameObject self) {
        CastAbilities cast=self.getComponent(CastAbilities.class);
        Ability ability=cast.getAbility(castID);
        float range=ability.range;
        GameObject target= Window.getScene().getGameObject(targetId);
        if(target!=null&&!target.isDead()){

            pos=target.transform.position;

        }else if(ability.requiresTarget){
            pos=null;
            done();
            return;
        }

        if(range==-1||Maf.distance(self.transform.position,pos)<=range) {
            cast.castAbility(castID, pos,target);
            done();
        }else{
            brain.priorityCommand(new MoveCommand(pos,range));
        }
    }


}
