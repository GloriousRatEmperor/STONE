package components;

import components.unitcapabilities.defaults.Sprite;
import jade.GameObject;
import util.Unit;

import java.util.List;

public class UNMADE {
    public String name;
    public float time;
    public float maxTime;
    public Sprite sprite;
    public UNMADE(String name) {
        this.name=name;
        this.maxTime=Unit.getBuildTime(name);
        this.time=this.maxTime;
        this.sprite=Unit.getSprite(name);
    }


    public void Imgui(int BuildSize, List<GameObject> activeGameObjects, int ID){
//        ImGui.tableNextColumn();
//        ImGui.pushID(ID);
//        Sprite Asprite=getSprite();
//        Asprite.setTexture(sprite.getTexture());
//        Vector2f[] AtexCoords = Asprite.getTexCoords();
//
//
//        if (ImGui.imageButton(Asprite.getTexId(), BuildSize, BuildSize, AtexCoords[2].x, AtexCoords[0].y, AtexCoords[0].x, AtexCoords[2].y)) {
//            List<Integer> Ids=new ArrayList<Integer>();
//            for (GameObject go:activeGameObjects) {
//                CastAbilities cast= go.getComponent(CastAbilities.class);
//                if (!(cast ==null)){
//                    if (cast.isCastable(type)){
//                        Ids.add(go.getUid());
//                        if (!KeyListener.isKeyPressed(GLFW_MOD_CONTROL)){
//                            break;
//                        }
//                    }
//                }
//            }
//            if (Ids.size()>0){
//                Window.sendCast(Ids,name);
//            }
//        }ImGui.popID();
    }
    public Sprite getSprite(){
        return sprite;
    }
    
}
