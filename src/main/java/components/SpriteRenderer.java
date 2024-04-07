package components;

import editor.JImGui;
import imgui.ImGui;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;
import util.AssetPool;

import java.util.List;

public class SpriteRenderer extends Component {
    public SpriteRenderer Clone(){
        return new SpriteRenderer();
    }

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    @Override
    public void start() {
        if (this.sprite.getTexture() != null) {
            this.sprite.setTexture(AssetPool.getTexture(this.sprite.getTexture().getFilepath()));
        }
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
    }
    @Override
    public void updateDraw(){
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void editorUpdateDraw() {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imgui() {
        if (JImGui.colorPicker4("Color Pickier", this.color)) {
            this.isDirty = true;
        }
    }
    @Override
    public List<GameObject> masterGui(List<GameObject> activegameObjects) {
        if (JImGui.colorPicker4("Color Pickier", this.color)) {
            this.isDirty = true;
            for (GameObject go : activegameObjects) {
                SpriteRenderer ccomp=go.getComponent(SpriteRenderer.class);
                if(ccomp!=null){
                    ccomp.color=this.color;
                    ccomp.isDirty=true;
                }

            }

        }
        return activegameObjects;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    public Vector4f getColor() {
        return this.color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }
    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(float r, float g, float b, float a) {
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!probably isn't rgba I'm guessing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (!this.color.equals(r,g,b,a)) {
            this.isDirty = true;
            this.color.set(r,g,b,a);
        }
    }
    public void setColorVec(Vector4f color) {
        //                                        only use if not new vector! apparently better performance maybe
        if (!this.color.equals(color)) {
            this.isDirty = true;
            this.color.set(color);
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }
}
