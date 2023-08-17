package components;

import physics2d.components.Rigidbody2D;

public class Mortal extends Component {
    public float health=20;
    public int allied =1;
    private transient Rigidbody2D rb;
    @Override
    public Mortal Clone(){
        return new Mortal();
    }
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);

    }
    public void takeDamage(float damage){
        health-=damage;
        if(health<0){
            die();
        }
    }


    public void die(){
        gameObject.destroy();


    }



}
