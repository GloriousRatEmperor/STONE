package components.unitcapabilities;

import components.Component;
import components.SubComponents.Commands.Command;
import components.SubComponents.Commands.MoveCommand;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import jade.Window;
import util.Maf;

import java.util.LinkedList;

public class Brain extends Component {
    private double stunTimer=0;
    private boolean afk=true;
    private boolean guard=true;

    private float pullrange=6f;

    private Command currentCommand=null;
    private LinkedList<Command> commandqueue=new LinkedList<>();

    public void setGuard(boolean state){
        if(guard!=state){
            guard=state;
            Mortal mortal=gameObject.getComponent(Mortal.class);
            if(mortal!=null){
                mortal.guardMode=state;
            }
            gameObject.getComponent(aggroDetector.class).setActive(state);
        }
    }
    @Override
    public void start() {
        Mortal mortal=gameObject.getComponent(Mortal.class);
        aggroDetector detect=gameObject.getComponent(aggroDetector.class);
        if((gameObject.getComponent(MoveContollable .class)!=null)&&(detect!=null)) {
            if(mortal!=null) {
                mortal.guardMode = false;
            }
            guard=false;
        }
    }
    public void setCommand(Command command){
        if(currentCommand!=null) {
            if (currentCommand.cancel()) {
                currentCommand = null;
            }
        }
        commandqueue.clear();
        commandqueue.add(command);
    }
    public void setAfk(boolean newstate){

        if(afk!=newstate&&!guard){
            gameObject.getComponent(aggroDetector.class).setActive(newstate);
        }
        afk=newstate;

    }
    public void addCommand(Command command){
        commandqueue.add(command);
    }
    public void considerMove(int enemyID){

        if(afk){
            GameObject enemy=Window.getScene().runningGetGameObject(enemyID);
            if(enemy!=null){
                if(enemy.allied!=gameObject.allied&& Maf.distance(gameObject.transform.position,enemy.transform.position)<=pullrange){
                    addCommand(new MoveCommand(enemy.transform));

                }
            }

        }
    }

    public void priorityCommand(Command command){
        commandqueue.addFirst(command);
    }

    public void notifyDone(){
        currentCommand=null;
    }
    @Override
    public void update(float dt){
        if(stunTimer>0) {
            stunTimer -= dt;
            return;
        }
        if(currentCommand==null){
            if(!commandqueue.isEmpty()) {
                setAfk(false);
                currentCommand = commandqueue.getFirst();
                currentCommand.setBrain(this);
                commandqueue.removeFirst();
                currentCommand.apply(gameObject);
            }else {
                setAfk(true);
            }
            return;
        }
        currentCommand.update(dt);

    }
}
