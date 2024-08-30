package components;

import physics2d.components.CircleCollider;

public class ShootCollider extends CircleCollider {


    public ShootCollider Clone() {
    return new ShootCollider();
    }
    public ShootCollider(){
        this.isSensor=true;
    }
}
