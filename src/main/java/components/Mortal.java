package components;

import physics2d.components.Rigidbody2D;

public class Mortal extends Component {
    public float health=30;
    public float maxHealth=30;

    public int alliedM =0;
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
    @Override
    public void begin(){
        this.alliedM = super.gameObject.allied;
    }

    public Mortal(float health){
        this.health = health;
        maxHealth = health;
    }
    public Mortal(){

    }

}
