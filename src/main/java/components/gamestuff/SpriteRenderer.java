package components.gamestuff;

import components.Component;
import components.unitcapabilities.defaults.Sprite;
import editor.JImGui;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;
import util.AssetPool;

import java.util.HashMap;
import java.util.List;

public class SpriteRenderer extends Component {
    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();
    public int shaderIndex=0;
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
    public void LevelEditorStuffImgui() {
        if (JImGui.colorPicker4("Color Pickier", this.color)!=null) {
            this.isDirty = true;
        }
    }
    @Override
    public List<GameObject> EditorGui(List<GameObject> activegameObjects, HashMap<String,String> data) {
        float[] diff=JImGui.colorPicker4("Color Pickier", this.color);
        if (diff!=null) {
            this.isDirty = true;
            for (GameObject go : activegameObjects) {

                SpriteRenderer ccomp=go.getComponent(SpriteRenderer.class);
                if(ccomp!=null) {
                    if (!ccomp.equals(this)) {
                        ccomp.color.add(diff[0], diff[1], diff[2], diff[3]);
                        ccomp.isDirty = true;
                    }
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
    public void setTexCoords(Vector2f[] newtex) {
        sprite.setTexCoords(newtex);
    }
    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(float r, float g, float b, float a) {
        if (!this.color.equals(r,g,b,a)) {
            this.isDirty = true;
            this.color.set(r,g,b,a);
        }
    }
    public void addColor(float color){
        this.color.add(color,color,color,color);
        setDirty();
    }
    public void multColor(float color){
        this.color.mul(color);
        setDirty();
    }
    public void addColor(float r, float g, float b, float a){
        this.color.add(r,g,b,a);
        setDirty();
    }
    public void multColor(float r, float g, float b, float a){
        this.color.mul(r,g,b,a);
        setDirty();
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
