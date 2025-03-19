package components.unitcapabilities;

import components.Component;
import components.UNMADE;
import components.gamestuff.SpriteRenderer;
import components.unitcapabilities.defaults.Sprite;
import imgui.ImGui;
import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;
import util.Unit;

import java.util.ArrayList;
import java.util.List;

import static jade.Window.getScene;
import static util.Img.Color;

public class UnitBuilder extends Component {
    public ArrayList<UNMADE> queue= new ArrayList<>();
    public int alliedB =0;
    private float buildSpeed=1;
    public transient Transform tr;
    public Sprite icon;

    @Override
    public void begin(){
//        super.isactive=true;
//        if(this.gameObject.getComponent(CastAbilities.class)==null){
//            this.gameObject.addComponent(new CastAbilities());
//        };
//        CastAbilities ability=this.gameObject.getComponent(CastAbilities.class);
//        builder=ability.getAbility("Build unit");
//        ability.addAbility(builder);
        if(this.alliedB==0){
            this.alliedB=super.gameObject.allied;
        }

    }
    public void start() {
        this.tr = gameObject.getComponent(Transform.class);
        if (icon == null) {
            this.icon = gameObject.getComponent(SpriteRenderer.class).getSprite();
        }
        if (this.icon.getTexture() != null) {
            this.icon.setTexture(AssetPool.getTexture(this.icon.getTexture().getFilepath()));
        }
    }
    @Override
    public void update(float dt){

        if(!queue.isEmpty()){
            UNMADE unm=queue.get(0);
            unm.time-=dt*buildSpeed;
            if(unm.time<0){
                makeUnit(unm);
                synchronized (queue){
                    queue.remove(unm);
                }

            }
        }


    }

    public void makeUnit(UNMADE unm) {
        Vector2f position = new Vector2f(tr.position.x,tr.position.y-0.001f);
        GameObject unit = Unit.makeUnit(unm.name,position, alliedB);
        getScene().addGameObjectToScene(unit);
    }
    public void addqueue(String UnitName, Vector3f cost){
        if(getScene().addmoney(-cost.x, -cost.y, -cost.z,alliedB)) {
            UNMADE unm = new UNMADE(UnitName);
            queue.add(unm);
            queue.sort((s1, s2) -> (int) (s1.time - s2.time));
        }
    }
//    @Override
//    public void copyProperties(Component master) {
//        for (UNMADE c :
//                queue) {
//            UNMADE clone = c.Clone();
//
//            Field[] fields = c.getClass().getDeclaredFields();
//            try {
//                for (Field field : fields) {
//
//                    boolean isTransient = Modifier.isTransient(field.getModifiers());
//                    boolean isFinal = Modifier.isFinal(field.getModifiers());
//                    if (isTransient || isFinal) {
//                        continue;
//                    }
//
//                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
//                    if (isPrivate) {
//                        field.setAccessible(true);
//                    }
//                    Object value = field.get(c);
//                    field.set(clone, value);
//                    if (isPrivate) {
//                        field.setAccessible(false);
//                    }
//                }
//            } catch (IllegalAccessException ex) {
//                System.out.println(c.getClass()+"is in illegal state.... whatever that means, good luck fixing me I guess");
//
//                throw new RuntimeException(ex);
//            }
//
//            ((UnitBuilder) master).queue.add(clone);
//        }
//
//    }

    @Override
    public String RunningGui(int size, List<GameObject> activeGameObjects, int ID) {
        ImGui.pushID(ID);

        int len=queue.size();
        synchronized (queue) {
            for (int i = 0; len > i; i++) {
                UNMADE unmade = queue.get(i);
                ImGui.tableNextColumn();
                Sprite Asprite = unmade.sprite;
                Vector2f[] AtexCoords = Asprite.getTexCoords();
                ImGui.image(Asprite.getTexId(), size, size, AtexCoords[2].x, AtexCoords[0].y, AtexCoords[0].x, AtexCoords[2].y);
                if (unmade.time != unmade.maxTime) {
                    float progressBarLen = ImGui.getItemRectMaxX() - 5 - (ImGui.getItemRectMinX() + 5);
                    ImGui.getWindowDrawList().addLine(ImGui.getItemRectMinX() + 5, ImGui.getItemRectMaxY() - 5, ImGui.getItemRectMinX() + 5 + progressBarLen * (1 - unmade.time / unmade.maxTime), ImGui.getItemRectMaxY() - 5, Color(255, 255, 255, 220), 5);
                }
            }
        }



        ImGui.popID();
        return null;

    }





}
