package jade;

import components.gamestuff.SpriteRenderer;
import components.gamestuff.Spritesheet;
import components.unitcapabilities.damage.Hitter;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.MoveContollable;
import components.unitcapabilities.defaults.Sprite;
import org.joml.Vector2f;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;
import util.AssetPool;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY, String name, Vector2f position) {
        GameObject block = Window.getScene().createGameObject(name,position);
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }


    public static GameObject generateUnit(Vector2f position,int allied) {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/back.png");
        GameObject newobject = generateSpriteObject(items.getSprite(0), 0.18f, 0.18f);
        newobject.transform.position = position;

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        newobject.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        newobject.addComponent(circleCollider);
        Hitter hit=new Hitter();
        hit.alliedH =allied;
        newobject.addComponent(hit);
        Mortal mortal=new Mortal();
        mortal.alliedM =allied;
        newobject.addComponent(mortal);
        newobject.addComponent(new MoveContollable());
        return newobject;
    }



}
