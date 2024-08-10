package Abilitiess;

import Multiplayer.ServerData;
import components.Mortal;
import jade.GameObject;
import util.Img;

public class Heal extends Ability{
    private float power=0.5f;
    @Override
    public Heal Copy(){
        Heal heal=new Heal(id);
        return heal;
    }
    public Heal( int id) {
        super(id);
        mp=100;
        sprite = Img.get("heal");
        setDesc("restores "+ Math.round(power*100)+"%% missing hp");

    }
    @Override
    public void cast(ServerData data, GameObject self) {
        Mortal mortal=self.getComponent(Mortal.class);
        if(mortal!=null){
            mortal.health+= power*(mortal.maxHealth-mortal.health);
        }
}
}
