package components.unitcapabilities;

import components.Component;
import components.SubComponents.Commands.Command;
import components.SubComponents.Commands.MoveCommand;
import components.SubComponents.SubComponent;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.MoveContollable;
import jade.GameObject;
import jade.Transform;
import jade.Window;
import org.joml.Vector2f;
import util.Maf;

import java.util.LinkedList;

public class Brain extends Component {
    public double stunTimer=0;
    public boolean afk=true;
    public boolean guard=true;

    public float pullrange=6f;

    protected transient Command currentCommand=null;
    protected transient LinkedList<Command> commandqueue=new LinkedList<>();
    public Brain() {
        subComponents = commandqueue;
    }
    public void setGuard(boolean state){
        if(guard!=state){
            guard=state;
            Mortal mortal=gameObject.getComponent(Mortal.class);
            if(mortal!=null){
                mortal.guardMode=state;
            }
            gameObject.getComponent(aggroDetector.class).setActive(!state);
        }
    }

    @Override
    public void start() {

        Mortal mortal=gameObject.getComponent(Mortal.class);
        aggroDetector detect=gameObject.getComponent(aggroDetector.class);
        if((gameObject.getComponent(MoveContollable .class)!=null)&&(detect!=null)&&(gameObject.getComponent(Worker.class)==null)) {
            if(mortal!=null) {
                mortal.guardMode = false;
            }
            guard=false;
        }else{
            guard=true;
        }

    }
    public void setCommand(Command command){
        interruptCommand();
        commandqueue.clear();
        commandqueue.add(command);
        update(0);
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
    public void considerAggro(int enemyID){

        if(afk){
            GameObject enemy=Window.getScene().getGameObjectRunning(enemyID);
            if(enemy!=null){
                if(enemy.allied!=gameObject.allied&& Maf.distance(gameObject.transform.position,enemy.transform.position)<=pullrange){
                    setCommand(new MoveCommand(enemy.transform));

                }
            }

        }
    }
    public void aggro(Transform enemy){
        priorityCommand(new MoveCommand(enemy,-100));
    }
    public MoveCommand moveCommand(Transform t,float tolerance,boolean amove){
        return new MoveCommand(t,tolerance,amove);
    }
    public MoveCommand moveCommand(Vector2f p, boolean amove){
        return new MoveCommand(p,amove);
    }
    public boolean interruptCommand(){ //returns true on successs
        if(stunTimer>0){
            return false;
        }
        if(currentCommand!=null){
            if(this.currentCommand.cancel()){
                commandqueue.addFirst(currentCommand);
                currentCommand=null;
                return true;
            }else{
                return false;
            }

        }
        return true;

    }
    public void priorityCommand(Command command){
        if(interruptCommand()){
            currentCommand=null;
            commandqueue.addFirst(command);
            update(0);
        }else{
            commandqueue.addFirst(command);
        }


    }

    public void notifyDone(){
        currentCommand=null;
        update(0);
    }
    @Override
    public void update(float dt){
        if(gameObject.isDead()){
            return;
        }

        if(currentCommand==null){
            if(stunTimer>0) {
                stunTimer -= dt;
                return;
            }
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
        currentCommand.update(dt,gameObject);
        stunTimer -= dt;

    }
    @Override
    public void addSubComponent(SubComponent c) {
        Command a=((Command)c);
        addCommand(a);
    }
}
