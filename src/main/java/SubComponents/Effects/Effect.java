package SubComponents.Effects;

import SubComponents.SubComponent;
import components.Sprite;
import imgui.ImGui;
import jade.GameObject;
import org.joml.Vector2f;
import util.AssetPool;

import java.util.List;
import java.util.Objects;

public abstract class Effect extends SubComponent {
    public float durationTotal;
    public float durationNow;
    public float power;
    public String description="dunno";
    public int id;
    protected Sprite sprite;
    public String name;
    public String type="";

    public Effect(float duration, float power) {
        imguiGroup =2;
        this.durationTotal = duration;
        this.durationNow = duration;
        this.power = power;
    }

    public void apply(GameObject self) {

    }

    public void updateDesc(){
        setDesc("this appears to be a mysetry effect of undefined effect");
    }
    public void setDesc(String description){
        if(!Objects.equals(type, "")) {
            this.description = description + "|4 type: " + type;
        }else{
            this.description=description;
        }
    }

    public void expire(GameObject self) {

    }

    public void update(float dt) {
        durationNow -= dt;
    }
    @Override
    public String RunningGui(int Size, List<GameObject> activeGameObjects, int ID){
        ImGui.tableNextColumn();

        Sprite Asprite=sprite;
        Vector2f[] AtexCoords = Asprite.getTexCoords();
        ImGui.image(Asprite.getTexId(), Size, Size, AtexCoords[2].x, AtexCoords[0].y, AtexCoords[0].x, AtexCoords[2].y);
        if(ImGui.isItemHovered(0)){
            return description+"|3 remaining duration: "+Math.round( durationNow);

        }else {

            return null;
        }

    }
    public void start(){
        if (sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
        }
    }
}


