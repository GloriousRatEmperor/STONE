package components.unitcapabilities;

import components.Component;
import components.SubComponents.Commands.MoveCommand;
import components.unitcapabilities.damage.Mortal;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;

public class aggroDetector extends Component {

    private float range=2f;
    public boolean active=true;
    private CircleCollider detectCircle;
    public aggroDetector(float range){
        this.range=range;
    }
    public aggroDetector(){

    }
    public void setActive(boolean newactive){
        if(active!=newactive){
            if(newactive){
                detectCircle.setEnabled();
                gameObject.getComponent(Rigidbody2D.class).setSleepAllowed(false);
            }else{
                detectCircle.setDisabled();
                gameObject.getComponent(Rigidbody2D.class).setSleepAllowed(true);
            }
            active=newactive;

        }
    }
    public void aggroTo(GameObject go){
        Brain brain=gameObject.getComponent(Brain.class);
        brain.setCommand(new MoveCommand(go.transform,-100));
        setActive(false);
    }
    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if(contact.m_fixtureB.getUserData().equals(detectCircle)||contact.m_fixtureA.getUserData().equals(detectCircle)) {
            if(collidingObject.getComponent(Mortal.class)!=null) {
                aggroTo(collidingObject);
            }
        }
    }
    @Override
    public void start(){
        super.start();
        detectCircle =new detectCollider();
        detectCircle.setRadius(range);
        detectCircle.collisionGroup=-2;
        gameObject.addComponent(detectCircle);
    }

}
