package components;

import physics2d.components.Rigidbody2D;

public class Mortal extends Component {
    public float health=30;
    public float maxHealth=30;
    public boolean isAlive=true;
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
        if(this.isAlive) {
            System.out.println(damage);
            health -= damage;
            if (health < 0) {
                death();
            }
        }
    }
    @Override
    public void die(){
        isAlive=false;
    }
    @Override
    public void destroy(){
        isAlive=false;
    }

    public void death(){
        isAlive=false;
        gameObject.die();
    }
    @Override
    public void begin(){
        if(this.alliedM==0){
            this.alliedM=super.gameObject.allied;
        }
    }

    public Mortal(float health){
        this.health = health;
        maxHealth = health;
    }
    public Mortal(){

    }

}
