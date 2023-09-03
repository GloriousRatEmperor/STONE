package SubComponents;

import components.Mortal;
import components.Spritesheet;
import enums.AbilityName;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2f;
import util.AssetPool;

import java.util.ArrayList;

public class Heal extends Ability{
    public Heal(AbilityName a, int id) {
        super(a, id);
        mp=100;
        Spritesheet AbilitySprites = AssetPool.getSpritesheet("assets/images/abilities.png");
        sprite = AbilitySprites.getSprite(2);
    }
    @Override
    public void Trigger(Vector2f position, ArrayList<Integer> id){
        Window.sendSelfcast(id,name);
    }
    @Override
    public void cast(Vector2f position, GameObject self) {
        Mortal mortal=self.getComponent(Mortal.class);
        if(mortal!=null){
            mortal.health+= 0.5*(mortal.maxHealth-mortal.health);
        }
}
}
