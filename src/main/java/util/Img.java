package util;

import components.gamestuff.MapSpriteSheet;
import components.unitcapabilities.defaults.Sprite;

public class Img {
    public static Sprite get(String name){
        MapSpriteSheet Sprites = AssetPool.getMapSheet("assets/images/spritesheets/joined.png");
        Sprite sprite=Sprites.getSprite(name);
//        if(sprite==null){
//            System.out.println("no such image as "+name);
//        }
        return sprite;
    }
    public static int color(int r, int g, int b, int a) { int ret = a; ret <<= 8; ret += b; ret <<= 8; ret += g; ret <<= 8; ret += r; return ret; }
}
