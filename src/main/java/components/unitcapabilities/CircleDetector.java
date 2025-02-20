package components.unitcapabilities;

import components.Component;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.Rigidbody2D;

public class CircleDetector  extends Component {

        public float range=2f;
        public boolean active=true;
        transient protected detectCollider detectCircle;
        public CircleDetector(){

        }
        public CircleDetector(float range){
            this.range=range;
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
        @Override
        public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
            if(!active){
                return; //just for when it's disabled right as it's making contact and stuff, should not happen too oft
            }
            if(contact.m_fixtureB.getUserData().equals(detectCircle)||contact.m_fixtureA.getUserData().equals(detectCircle)) {
                objectDetected(collidingObject);
            }
        }
    public void objectDetected(GameObject collidingObject){

    }
    @Override
        public void start(){
            super.start();
            detectCircle =new detectCollider();
            detectCircle.setRadius(range);
            detectCircle.collisionGroup=-2;
            detectCircle.gameObject=gameObject;
            detectCircle.begin();
        }
        @Override
        public void destroy(){
            if(detectCircle!=null){
                detectCircle.destroy();
            }
        }


    }


