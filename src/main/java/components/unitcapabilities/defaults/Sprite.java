package components.unitcapabilities.defaults;

import org.joml.Vector2f;
import renderer.Texture;

public class Sprite implements Cloneable{
    public Sprite clone() throws CloneNotSupportedException {
        Sprite clone=((Sprite) super.clone());
        clone.setTexture(texture.clone());
        clone.setTexCoords(texCoords.clone());
        return clone;
    }
    private float width, height;

    private Texture texture = null;
    private Vector2f[] texCoords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setTexture(Texture tex) {
        this.texture = tex;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    public int getTexId() {
        return texture == null ? -1 : texture.getId();
    }
}
