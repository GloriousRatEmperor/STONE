package components.unitcapabilities;

import physics2d.components.CircleCollider;

public class detectCollider extends CircleCollider {

    public detectCollider(){
        this.isSensor=true;
    }
}
