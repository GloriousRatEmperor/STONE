package components.unitcapabilities;

import components.SubComponents.Commands.MoveCommand;
import components.SubComponents.Commands.SkirmishCommand;
import jade.GameObject;
import jade.Transform;
import jade.Window;
import util.Maf;

public class RangedBrain extends Brain{
    public boolean skirmish=true;
    public float range;
    public RangedBrain(){
        super();

    }
    @Override
    public void start(){
        Shooter shot=gameObject.getComponent(Shooter.class);
        if(shot==null){
            System.out.println("what is a friggin close ranger doing with skirmish brain??");
        }else{
            range= shot.range;
        }
        super.start();
    }

    public void setSkirmish(boolean state){
        if(skirmish!=state){
            skirmish=state;
        }
    }
    @Override
    public void considerAggro(int enemyID){
        if(afk){
            GameObject enemy= Window.getScene().runningGetGameObject(enemyID);
            if(enemy!=null){
                if(enemy.allied!=gameObject.allied&& Maf.distance(gameObject.transform.position,enemy.transform.position)<=pullrange){
                    setCommand(new SkirmishCommand(enemy.transform,range*0.875f,range*0.99f));

                }
            }
        }
    }
    @Override
    public void aggro(Transform enemy){
        priorityCommand(new SkirmishCommand(enemy,range*0.875f,range*0.99f));
    }
    @Override
    public MoveCommand moveCommand(Transform t, float tolerance, boolean amove){

        if(!skirmish){
            return super.moveCommand(t,tolerance,amove);
        }else{
            return new SkirmishCommand(t,amove,range*0.875f,range*0.99f);
        }

    }

}
