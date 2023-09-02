package jade;

import components.Component;
import components.SpriteRenderer;
import components.StateMachine;
import editor.JImGui;
import org.joml.Vector2f;
import renderer.Texture;

import java.util.List;

public class Transform extends Component {
    public Transform Clone(){
        return new Transform();
    }
    public Vector2f position;
    public Vector2f drawPos;
    public Vector2f pastPos;
    public Vector2f scale;
    public float rotation = 0.0f;
    public int zIndex;
    public void updatePastPos(){pastPos=new Vector2f(position);};
    public void updateDrawPos(float fraction){

        drawPos.x=pastPos.x* (1-fraction)+position.x * fraction;
        drawPos.y=pastPos.y* (1-fraction)+position.y * fraction;


    };
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
        this.position = position;
        this.drawPos = position;
        this.pastPos = position;
        this.scale = scale;
        this.zIndex = 0;
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    @Override
    public List<GameObject> masterGui(List<GameObject> activegameObjects) {
        String name=JImGui.inputText("Name: ", gameObject.name);
        if(gameObject.name != name){
            gameObject.name = name;
            for (GameObject go : activegameObjects) {
                Transform ccomp=go.getComponent(Transform.class);
                if(ccomp!=null){
                    ccomp.gameObject.name=name;
                }
            }
        }
        Vector2f pos=new Vector2f(position.x,position.y);
        JImGui.drawVec2Control("Position", this.position);
        if(pos!=this.position){
            for (GameObject go : activegameObjects) {
                Transform ccomp=go.getComponent(Transform.class);
                if(ccomp!=null){

                    ccomp.position.x-=pos.x-this.position.x;
                    ccomp.position.y-=pos.y-this.position.y;
                }
            }
        }

        Vector2f siz=new Vector2f(this.scale.x,this.scale.y);
        JImGui.drawVec2Control("Scale", this.scale, 32.0f);
        if(siz!=this.scale){
            for (GameObject go : activegameObjects) {
                Transform ccomp=go.getComponent(Transform.class);
                if(ccomp!=null){

                    ccomp.scale.x-=siz.x-this.scale.x;
                    ccomp.scale.y-=siz.y-this.scale.y;
                }
            }
        }


        float rot = JImGui.dragFloat("Rotation", this.rotation);
        if(this.rotation!=rot) {
            for (GameObject go : activegameObjects) {
                Transform ccomp=go.getComponent(Transform.class);
                if(ccomp!=null){
                    ccomp.rotation+=rot-this.rotation;
                }
            }
            this.rotation = rot;
        }
        int index=JImGui.dragInt("Z-Index", this.zIndex);
        if(this.zIndex !=index ) {

            for (GameObject go : activegameObjects) {
                Transform ccomp=go.getComponent(Transform.class);
                if(ccomp!=null){
                    ccomp.zIndex+=index-this.zIndex;
                }
            }
            this.zIndex = index;
        }


        return activegameObjects;
    }

    @Override
    public void imgui() {
        gameObject.name = JImGui.inputText("Name: ", gameObject.name);
        JImGui.drawVec2Control("Position", this.position);
        JImGui.drawVec2Control("Scale", this.scale, 32.0f);
        this.rotation = JImGui.dragFloat("Rotation", this.rotation);
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
}
