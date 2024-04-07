package components;

import org.joml.Vector2f;
import org.joml.Vector4i;
import renderer.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapSpriteSheet {

    private Texture texture;
    private HashMap<String,Vector4i> map;
    private HashMap<String,Sprite> sprites;

    public MapSpriteSheet(Texture texture, HashMap<String,Vector4i> mappa) {
        this.sprites = new HashMap<>();
        map=mappa;
        this.texture = texture;
        Vector4i coord;
        for (String key: mappa.keySet()) {
            coord=mappa.get(key);
            float topY = (coord.w + coord.y) / (float)texture.getHeight();
            float rightX = (coord.z + coord.x) / (float)texture.getWidth();
            float leftX = coord.z / (float)texture.getWidth();
            float bottomY = coord.w / (float)texture.getHeight();

            Vector2f[] texCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };
            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(coord.x);
            sprite.setHeight(coord.y);
            this.sprites.put(key,sprite);
            }
        }


    public Sprite getSprite(String name) {
        return sprites.get(name);
    }


    public List<Sprite> getSprites() {
        List<Sprite> sprit=new ArrayList<>();
        for(String key:sprites.keySet()){
            sprit.add(this.sprites.get(key));
        }
        return sprit;
    }

    public int size() {
        return sprites.size();
    }
}
