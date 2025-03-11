package components.unitcapabilities;

import physics2d.components.CircleCollider;

public class CircleSensor extends Sensor {

        public float range=2f;
        public CircleSensor(float range){

            this.range=range;
        }
        public CircleSensor(){
        }
        @Override
        public void startSensor(){
            sensor =new CircleCollider();
            sensor.isSensor=true;
            ((CircleCollider) sensor).setRadius(range);
            sensor.collisionGroup=-2;
            sensor.gameObject=gameObject;
            sensor.begin();


        }


}


