package SubComponents;

import components.Spritesheet;
import enums.AbilityName;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2f;
import physics2d.components.MoveContollable;
import physics2d.components.Rigidbody2D;
import util.AssetPool;

import java.util.ArrayList;

public class Speed extends Ability{
    public Speed(AbilityName a, int id) {
        super(a, id);
        mp=20;
        Spritesheet AbilitySprites = AssetPool.getSpritesheet("assets/images/abilities.png");
        sprite = AbilitySprites.getSprite(1);
    }
    @Override
    public void Trigger(Vector2f position, ArrayList<Integer> id){
        Window.sendSelfcast(id,name);
    }
    @Override
    public void cast(Vector2f position, GameObject self) {
        System.out.println("AAAAAAAA yee");
        System.out.println("AAAAAAAA yee");
        MoveContollable movement = self.getComponent(MoveContollable.class);
        if (movement != null) {
            movement.speed *= 2;
        }
        Rigidbody2D currentMove = self.getComponent(Rigidbody2D.class);
        if (currentMove != null) {
            currentMove.addVelocity(currentMove.getVelocity());

        }
    }
}
