package components.unitcapabilities;

import components.Component;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

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
            //System.out.println("activing "+newactive);
            if(active!=newactive){
                //System.out.println("activing "+newactive);
                if(newactive){
                    detectCircle.setEnabled();
                }else{
                    detectCircle.setDisabled();
                }
                active=newactive;

            }
        }
        @Override
        public void update(float dt){
            detectCircle.update(dt);


        }
        @Override
        public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
            if(!active){
                return;
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


