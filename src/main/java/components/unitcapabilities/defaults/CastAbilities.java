package components.unitcapabilities.defaults;

import components.Component;
import components.SubComponents.Abilities.*;
import components.SubComponents.SubComponent;
import enums.AbilityName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import jade.Transform;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

import static enums.AbilityName.StringToAbilityName;
import static renderer.DebugDraw.addLine2D;

public class CastAbilities extends Component {
    public float mp = 0f;
    public float mpRegen = 0.01f;
    public float maxmp = 0f;
    public transient List<Ability> abilities = new ArrayList<>();



    public CastAbilities() {
        subComponents = abilities;
    }

    public Boolean isCastable(int id) {
        for (Ability a : abilities) {
            if (a.getID()== id) {
                return a.Castable(mp) && gameObject.allied == Window.get().allied;
            }
        }
        return false;
    }

    public Boolean containsAbility(int id) {
        for (Ability a : abilities) {
            if (a.getID()== id) {
                return true;
            }
        }
        return false;
    }

    public Boolean containsAbility(AbilityName aName) {
        for (Ability a : abilities) {
            if (Objects.equals(a.type, aName)) {
                return true;
            }
        }
        return false;
    }

    public void castAbility(int id,Vector2f pos,GameObject target) {
        for (Ability a : abilities) {
            if (a.getID() == id) {
                if (a.Castable(mp)) {
                    if(a.cast(pos, super.gameObject,target)){
                        mp -= a.mp;
                    }

                    break;
                }
            }
        }

    }



    @Override
    public void copyProperties(Component master) {
        for (Ability a :
                abilities) {
            if (!((CastAbilities) master).containsAbility(a.getID())) {
                Ability copy = a.Copy();
                copy.description = a.description;
                ((CastAbilities) master).addAbility(copy);
            }

        }
    }


    public Ability getAbility(int id) {
        for (Ability a : abilities) {
            if (a.getID()== id) {
                return a;
            }
        }
        return null;
    }

    public Ability makeAbility(AbilityName a) {
        Ability ability = switch (a) {
            case move -> new Move(a);
            case speed -> new Speed(a);
            case heal -> new Heal(a);
            case buildRock -> new BuildUnit(a,"rock");
            case buildwraith -> new BuildUnit(a,"wraith");
            case getBuildHeadlessHorseman -> new BuildUnit(a,"headlesshorseman");
            case buildHeadless->new BuildUnit(a,"headless");
            case buildPriest->new BuildUnit(a,"Priest");
            case buildGreenBarracks -> new BuildBuilding(a,"greenbarracks");
            case buildMorticum -> new BuildBuilding(a,"morticum");
            case buildSnek ->new BuildUnit(a,"snek");
            case buildBase -> new BuildBase(a);
            case buildBaseA -> new BuildBase(a,4);
            case buildBaseR -> new BuildBase(a,1);
            case buildBaseG -> new BuildBase(a,2);
            case buildBaseB -> new BuildBase(a,3);
            case buildTank -> new BuildUnit(a,"tank");
            case buildBarracks -> new BuildBuilding(a,"barracks");
            case buildPeasant -> new BuildUnit(a,"peasant");
            case buildWhitler -> new BuildUnit(a,"whitler");
            case buildWisp -> new BuildUnit(a,"wisp");
            case buildBoarCavalary -> new BuildUnit(a,"boarcavalary");
            case buildSpearman -> new BuildUnit(a,"spearman");
            case buildChicken -> new BuildUnit(a,"chicken");
            case buildPebble ->new BuildUnit(a,"pebble");
            case guardMode ->new GuardMode(a);
            case buildSpider -> new BuildUnit(a,"spider");
            case buildTotem ->new BuildUnit(a,"drownedtotem");
            case buildStoneborn -> new BuildUnit(a,"stoneborn");
            case buildBuffBird -> new BuildUnit(a,"buffbird");
            case buildVolcano -> new BuildUnit(a,"volcano");
            case errupt -> new Errupt(a);
            case buildAlterator -> new BuildUnit(a,"Alterator");
            case Alterate -> new Alterate(a);
            case shootAlterBolt -> new ShootAlterBolt(a);
            case teleportMarked -> new TeleportMarked(a);
            default -> null;

        };
        if (ability == null) {
            System.out.println("OOPS WE DON'T HAVE NO SUCH ABILITY TYPE " + a + " HERE");
            return null;
        }

        return ability;
    }

    public void addAbility(Ability ability) {
        if (ability == null) {
            System.out.println("wtf you tyina add bruv?");
        }  else {
            ability.owner = this;
            abilities.add(ability);
            abilities.sort(Comparator.comparingInt(Ability::getID));
        }
    }
    public List<Integer> consider(){
        List<Integer> casts=new ArrayList<>();
        for(Ability a:abilities){
            if(isCastable(a.getID())){
                if(a.consider(gameObject,mp==maxmp)){
                    casts.add(a.getID());
                }
            }

        }
        return casts;
    }

    public void addAbility(AbilityName a) {
        Ability ability = makeAbility(a);
        if (ability == null) {
            System.out.println("wtf you tyina add bruv?");
        } else {
            abilities.add(ability);
            abilities.sort(Comparator.comparingInt(Ability::getID));
        }
    }
    @Override
    public void update(float dt){
        mp+= Math.min(mpRegen,maxmp-mp);
    }

    @Override
    public List<GameObject> EditorGui(List<GameObject> activegameObjects, HashMap<String, String> guiData) {
        super.EditorGui(activegameObjects, guiData);
        String[] enumValues = getEnumValues(AbilityName.class);
        ImInt index = new ImInt(0);


        if (ImGui.combo("Add Ability", index, enumValues, enumValues.length)) {
            if (index.get() != 0) {
                for (GameObject go : activegameObjects) {
                    CastAbilities cast = go.getComponent(CastAbilities.class);
                    if (cast != null) {
                        AbilityName name = AbilityName.class.getEnumConstants()[index.get()];
                        if (!cast.containsAbility(name)) {
                            cast.addAbility(cast.makeAbility(name));
                            guiData.put("guiRemoveAbility", guiData.get("guiRemoveAbility") + '0' + name);
                        }
                    }

                }
            }
        }
        ImInt inde = new ImInt(0);
        String[] s = guiData.get("guiRemoveAbility").split("0");
        if (ImGui.combo("Remove Ability", inde, s, s.length)) {
            String name = s[inde.get()];
            int id=StringToAbilityName(name).getId();
            if (inde.get() != 0) {
                for (GameObject go : activegameObjects) {
                    CastAbilities cast = go.getComponent(CastAbilities.class);
                    if (cast != null) {
                        cast.removeAbility(id);
                    }
                    guiData.put("guiRemoveAbility", guiData.get("guiRemoveAbility").replace('0' + name, ""));

                }
            }
            this.removeAbility(id);
        }
        return activegameObjects;
    }

    public void removeAbility(int id) {
        for (Ability a : abilities) {
            if (a.getID()==id) {
                abilities.remove(a);
                break;
            }
        }

    }


    @Override
    public void updateDraw(){
        Transform tra=gameObject.transform;

        addLine2D(new Vector2f( tra.drawPos.x-tra.scale.x/2,tra.drawPos.y+tra.scale.y/2+0.05f), new Vector2f( tra.drawPos.x+(tra.scale.x/2)*(mp/maxmp*2-1),tra.drawPos.y+tra.scale.y/2+0.05f), new Vector3f(0,0,1), 1);

    }

    @Override
    public void updateData(HashMap<String, String> guiData, boolean running) {
        if (!running) {
            StringBuilder alreadyHas = new StringBuilder(guiData.getOrDefault("guiRemoveAbility", ""));

            if (Objects.equals(alreadyHas.toString(), "")) {
                alreadyHas.append("Remove ability");
            }
            List<String> names = new ArrayList<>(List.of(alreadyHas.toString().split("0")));
            for (Ability a : abilities) {
                if (!names.contains(a.type.name())) {
                    alreadyHas.append('0').append(a.type.name());
                }
            }
            guiData.put("guiRemoveAbility", alreadyHas.toString());

        }
    }

    @Override

    public void destroy() {
        this.abilities.clear();
    }

    @Override
    public void addSubComponent(SubComponent c) {
    Ability a=((Ability)c);
    addAbility(a);
    }
}
