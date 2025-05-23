package editor;

import components.Component;
import components.gamestuff.PlayerBot;
import components.gamestuff.StateMachine;
import components.unitcapabilities.*;
import components.unitcapabilities.damage.Hitter;
import components.unitcapabilities.damage.Mortal;
import components.unitcapabilities.defaults.CastAbilities;
import components.unitcapabilities.defaults.Effects;
import components.unitcapabilities.defaults.MoveContollable;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import jade.GameObject;
import jade.MasterObject;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
    private List<GameObject> activeGameObjects;
    private GameObject activeGameObject = null;
    private MasterObject MasterObject=new MasterObject(false);
    private List<Integer> ids=new ArrayList<>();
    private PickingTexture pickingTexture;
    private String[] componentNames=new String[]{};

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
    }

    public void imgui() {

        if (activeGameObjects.size() > 0 && activeGameObjects.get(0) != null) {

            ImGui.begin("Properties" , ImGuiWindowFlags.NoDocking);

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Basic stuff")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(CastAbilities.class) == null) {
                            go.addComponent(new CastAbilities());
                        }
                    }
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(Mortal.class) == null) {
                            go.addComponent(new Mortal());
                        }
                    }
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(MoveContollable.class) == null) {
                            go.addComponent(new MoveContollable());
                        }
                    }
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(Hitter.class) == null) {
                            go.addComponent(new Hitter());
                        }
                    }
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(CircleCollider.class) == null) {
                            if (go.getComponent(Box2DCollider.class) == null) {
                                go.addComponent(new CircleCollider());
                            }

                        }
                    }
                    for(GameObject go : activeGameObjects) {
                        if (go.getComponent(Effects.class) == null) {
                            go.addComponent(new Effects());
                        }
                    }
                    for(GameObject go : activeGameObjects) {
                        if (go.getComponent(Brain.class) == null) {
                            go.addComponent(new Brain());
                        }
                    }


                }
                if (ImGui.menuItem("Add StateMachine")) {
                    for (GameObject go : activeGameObjects) {
                        if(go.getComponent(StateMachine.class)==null){
                            go.addComponent(new StateMachine());
                        }
                    }
                }
                if (ImGui.menuItem("Add MoveControllable")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(MoveContollable.class) == null) {
                            go.addComponent(new MoveContollable());
                        }
                    }
                }
                if (ImGui.menuItem("Add PlayerBot")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(PlayerBot.class) == null) {
                            go.addComponent(new PlayerBot());
                        }
                    }
                }
                if (ImGui.menuItem("Add Worker")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(Worker.class) == null) {
                            go.addComponent(new Worker());
                        }
                    }
                }
                if (ImGui.menuItem("Add Abilities")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(CastAbilities.class) == null) {
                            go.addComponent(new CastAbilities());
                        }
                    }
                }
                    if (ImGui.menuItem("Add Mortal")) {
                        for (GameObject go : activeGameObjects) {
                            if (go.getComponent(Mortal.class) == null) {
                                go.addComponent(new Mortal());
                            }
                        }
                    }
                if (ImGui.menuItem("Add UnitBuilder")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(UnitBuilder.class) == null) {
                            go.addComponent(new UnitBuilder());
                        }
                    }
                }if (ImGui.menuItem("Add Shooter")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(Shooter.class) == null) {
                            go.addComponent(new Shooter());
                        }
                    }
                }
                if (ImGui.menuItem("Add Hitter")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(Hitter.class) == null) {
                            go.addComponent(new Hitter());
                        }
                    }
                }
                if (ImGui.menuItem("Add Rigidbody")) {
                    for (GameObject go : activeGameObjects) {
                    if (go.getComponent(Rigidbody2D.class) == null) {
                        go.addComponent(new Rigidbody2D());
                    }
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(Box2DCollider.class) == null) {

                            if (go.getComponent(CircleCollider.class) != null) {
                                go.removeComponent(CircleCollider.class);
                            }
                        }
                        go.addComponent(new Box2DCollider());
                    }
                }


                if (ImGui.menuItem("Add Circle Collider")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(CircleCollider.class) == null) {
                            if (go.getComponent(Box2DCollider.class) != null) {
                                go.removeComponent(Box2DCollider.class);
                            }
                            go.addComponent(new CircleCollider());
                        }
                    }
                }
                if (ImGui.menuItem("Add Effects")) {
                    for(GameObject go : activeGameObjects) {
                        if (go.getComponent(Effects.class) == null) {
                            go.addComponent(new Effects());
                        }
                    }
                }
                if (ImGui.menuItem("Add Brain")) {
                    for(GameObject go : activeGameObjects) {
                        if (go.getComponent(Brain.class) == null) {
                            go.addComponent(new Brain());
                        }
                    }
                }
                if (ImGui.menuItem("Add RangedBrain")) {
                    for(GameObject go : activeGameObjects) {
                        if (go.getComponent(RangedBrain.class) == null) {
                            go.removeComponent(Brain.class);
                            go.addComponent(new RangedBrain());
                        }
                    }
                }
                if (ImGui.menuItem("Add Base")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(Base.class) == null) {
                            go.addComponent(new Base());
                        }
                    }
                }
                ImGui.endPopup();
            }
            ImInt inde=new ImInt(0);


            if (ImGui.combo("Remove Component",inde, componentNames)) {
                if(inde.get()!=0) {


                    Class<? extends Component> cla=MasterObject.getAllComponents().get(inde.get()-1).getClass();
                    for (GameObject go : activeGameObjects) {
                            go.removeComponent(cla);

                    }
                    MasterObject.removeComponent(cla);
                    updateComponentName();
                }
        }

            MasterObject.EditorGui(activeGameObjects);




            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObjects.size() > 0 ? this.activeGameObjects.get(0) :
                null;
    }

    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void clearSelected() {
        this.MasterObject.clear();
        this.ids=new ArrayList<>();
        this.activeGameObjects.clear();
    }

    public void setActiveGameObject(GameObject go) {
        clearSelected();

        if (go != null) {
            addActiveGameObject(go);
        }
    }
    private void updateComponentName(){
        List<String> names = new ArrayList<>();
        names.add("Remove component");
        for (Component a :MasterObject.getAllComponents()){
            names.add(a.getClass().toString());

        }
        componentNames = new String[names.size()];
        componentNames = names.toArray(componentNames);
    }
    public void addActiveGameObject(GameObject go) {
        MasterObject.addGameObject(go);

        this.activeGameObjects.add(go);

        ids.add(go.getUid());
        updateComponentName();
    }
    public void removeActiveGameObject(GameObject go) {
        this.activeGameObjects.remove(go);
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
