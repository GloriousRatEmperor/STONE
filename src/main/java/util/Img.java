package util;

import components.MapSpriteSheet;
import components.Sprite;

public class Img {
    public static Sprite get(String name){
        MapSpriteSheet Sprites = AssetPool.getMapSheet("assets/images/spritesheets/joined.png");
        return Sprites.getSprite(name);
    }
}
