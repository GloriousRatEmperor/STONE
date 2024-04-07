package Abilitiess;

import Multiplayer.ServerData;
import components.Mortal;
import components.Spritesheet;
import jade.GameObject;
import util.AssetPool;

public class Heal extends Ability{
    @Override
    public Heal Copy(){
        Heal heal=new Heal(this.name,id);
        return heal;
    }
    public Heal(String a, int id) {
        super(a,id);
        mp=100;
        Spritesheet AbilitySprites = AssetPool.getSpritesheet("assets/images/abilities.png");
        sprite = AbilitySprites.getSprite(2);
    }
    @Override
    public void cast(ServerData data, GameObject self) {
        Mortal mortal=self.getComponent(Mortal.class);
        if(mortal!=null){
            mortal.health+= 0.5*(mortal.maxHealth-mortal.health);
        }
}
}
