package components.unitcapabilities;

import components.Component;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.Collider;

public abstract class Sensor extends Component {
    public boolean active=true;
    transient protected Collider sensor;
    public void setActive(boolean newactive){
        if(active!=newactive){
            if(newactive){
                sensor.setEnabled();
            }else{
                sensor.setDisabled();
            }
            active=newactive;

        }
    }
    @Override
    public void update(float dt){

        sensor.update(dt);



    }
    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

        if(!active){
            return;
        }
        if(contact.m_fixtureB.getUserData().equals(sensor)||contact.m_fixtureA.getUserData().equals(sensor)) {
            objectDetected(collidingObject);
        }
    }
    public void objectDetected(GameObject collidingObject){

    }
    @Override
    public void start(){
        super.start();
        startSensor();

    }
    public void startSensor(){

    }
    @Override
    public void destroy(){
        if(sensor !=null){
            sensor.destroy();
        }
    }
}
