package editor;

import components.*;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import jade.GameObject;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.MoveContollable;
import physics2d.components.Rigidbody2D;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
    private List<GameObject> activeGameObjects;
    private GameObject activeGameObject = null;
    private GameObject MasterObject=new GameObject("MasterObject");
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
                        if (go.getComponent(MoveContollable.class) == null) {
                            go.addComponent(new MoveContollable());
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
                            if (go.getComponent(Box2DCollider.class) != null) {
                                go.removeComponent(Box2DCollider.class);
                            }
                            go.addComponent(new CircleCollider());
                        }
                    }
                    for(GameObject go : activeGameObjects) {
                        if (go.getComponent(Effects.class) == null) {
                            go.addComponent(new Effects());
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
                if (ImGui.menuItem("Add Abilities")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(CastAbilities.class) == null) {
                            go.addComponent(new CastAbilities());
                        }
                    }
                }
                    if (ImGui.menuItem("Add Mortal")) {
                        for (GameObject go : activeGameObjects) {
                            if (go.getComponent(MoveContollable.class) == null) {
                                go.addComponent(new MoveContollable());
                            }
                        }
                    }
                if (ImGui.menuItem("Add UnitBuilder")) {
                    for (GameObject go : activeGameObjects) {
                        if (go.getComponent(UnitBuilder.class) == null) {
                            go.addComponent(new UnitBuilder());
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
            };

            activeGameObjects=MasterObject.editMasterGui(activeGameObjects);




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
        this.MasterObject=new GameObject("MasterObject");
        this.ids=new ArrayList<>();
        this.activeGameObjects.clear();
    }

    public void setActiveGameObject(GameObject go) {

        if (go != null) {
            clearSelected();
            this.activeGameObjects.add(go);
            MasterObject=go.mengui(MasterObject);
            ids.add(go.getUid());
            updateComponentName();
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
        this.activeGameObjects.add(go);
        MasterObject=go.mengui(MasterObject);
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
