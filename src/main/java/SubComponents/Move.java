package SubComponents;

import components.Spritesheet;
import enums.AbilityName;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2f;
import util.AssetPool;

import java.util.ArrayList;

public class Move extends Ability{
    public Move(AbilityName a, int id) {
        super(a, id);
        mp=0;
        Spritesheet AbilitySprites = AssetPool.getSpritesheet("assets/images/abilities.png");
        sprite = AbilitySprites.getSprite(0);
    }
    @Override
    public void Trigger(Vector2f position, ArrayList<Integer> id){
        Window.sendMove(position,id);
    }
}
