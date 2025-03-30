package jade;

import components.Component;
import components.gamestuff.SpriteRenderer;
import editor.JImGui;
import org.joml.Vector2f;

public class Transform extends Component {

    public Vector2f position;
    public Vector2f drawPos;
    public Vector2f drawOffset=new Vector2f();
    private boolean flippedX = false;
    private boolean flippedY = false;
    public Vector2f pastPos;
    public Vector2f scale;
    public float rotation = 0.0f;
    public float drawRotation = 0.0f; //added to rotation when drawing useful for stuff like wraithball when the projectile is spinning but also aimed at the enemy
    public int zIndex;
    public void updatePastPos(){
        pastPos.set(position);
    }
    @Override
    public void editorUpdateDraw(){

        updatePastPos();
    }
    public void updateDrawPos(float fraction){
        if(drawPos.x+drawOffset.x!=pastPos.x||pastPos.y+drawOffset.y!=drawPos.y){
            drawPos.set(pastPos).add(drawOffset);
            gameObject.getComponent(SpriteRenderer.class).setDirty();
        }


//        drawPos.x=pastPos.x* (1-fraction)+position.x * fraction;
//        drawPos.y=pastPos.y* (1-fraction)+position.y * fraction;




    }

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale) {

        this.position =new Vector2f(position);
        this.drawPos = new Vector2f(position);
        this.pastPos = new Vector2f(position);
        this.scale = scale;
        this.zIndex = 0;
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    @Override
    public void LevelEditorStuffImgui() {
        gameObject.name = JImGui.inputText("Name: ", gameObject.name);
        JImGui.drawVec2Control("Position", this.position);
        JImGui.drawVec2Control("Scale", this.scale, 32.0f);
        this.rotation = JImGui.dragFloat("Rotation", this.rotation);
        this.drawRotation = JImGui.dragFloat("Rotation", this.rotation);
        this.zIndex = JImGui.dragInt("Z-Index", this.zIndex);
    }
    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return t.position.equals(this.position) && t.scale.equals(this.scale) &&
                t.rotation == this.rotation && t.zIndex == this.zIndex;
    }
    public boolean isflippedY() {

        return flippedY;
    }
    public void setFlippedY(boolean direction) {
        if(flippedY!=direction){
           flipY();
        }

    }
    public void flipX() {
//        Vector2f[] texCoords= gameObject.getComponent(SpriteRenderer.class).getTexCoords();
//        float mi=texCoords[0].x;
//        texCoords[0].x = texCoords[2].x;
//        texCoords[1].x = texCoords[3].x;
//        texCoords[2].x = mi;
//        texCoords[3].x = mi;
//        gameObject.getComponent(SpriteRenderer.class).setTexCoords(texCoords);
        flippedX=!flippedX;
    }
    public boolean isflippedX() {
        return flippedX;
    }
    public void setFlippedX(boolean direction) {

        if(flippedX!=direction){
            flipX();
        }
    }
    public void flipY() {
        flippedY=!flippedY;
    }
}
