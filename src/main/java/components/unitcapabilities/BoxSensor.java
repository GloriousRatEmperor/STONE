package components.unitcapabilities;

import physics2d.components.Box2DCollider;

public class BoxSensor extends Sensor{
    public float sizeX=2f;
    public float sizeY=2f;
    public BoxSensor(float sizex,float sizey){
        sizeX= sizex;
        sizeY=sizey;
    }
    public BoxSensor(){
    }
    @Override
    public void startSensor(){
        sensor =new Box2DCollider();
        sensor.isSensor=true;
        ((Box2DCollider) sensor).setHalfSize(sizeX,sizeY);
        sensor.collisionGroup=-2;
        sensor.gameObject=gameObject;

        sensor.begin();


    }
}
