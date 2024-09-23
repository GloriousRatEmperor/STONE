package components;

import Multiplayer.ServerData;
import SubComponents.Abilities.*;
import SubComponents.SubComponent;
import enums.AbilityName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import jade.Window;

import java.util.*;

public class CastAbilities extends Component {
    private float mp = 100f;
    private float maxmp = 100f;
    public transient List<Ability> abilities = new ArrayList<>();

    @Override
    public CastAbilities Clone() {
        CastAbilities ablits = new CastAbilities();
        copyProperties(ablits);
        return ablits;
    }

    public CastAbilities() {
        subComponents = abilities;
    }

    public Boolean isCastable(int id) {
        for (Ability a : abilities) {
            if (Objects.equals(a.id, id)) {
                return a.Castable(mp) && gameObject.allied == Window.get().allied;
            }
        }
        return false;
    }

    public Boolean containsAbility(int id) {
        for (Ability a : abilities) {
            if (Objects.equals(a.id, id)) {
                return true;
            }
        }
        return false;
    }

    public Boolean containsAbility(AbilityName aName) {
        for (Ability a : abilities) {
            if (Objects.equals(a.id, aName.getId())) {
                return true;
            }
        }
        return false;
    }

    public void castAbility(ServerData data) {
        for (Ability a : abilities) {
            if (a.id == data.getIntValue()) {
                if (a.Castable(mp)) {
                    a.cast(data, super.gameObject);
                    mp -= a.mp;
                }
            }
        }

    }



    @Override
    public void copyProperties(Component master) {
        for (Ability a :
                abilities) {
            if (!((CastAbilities) master).containsAbility(a.id)) {
                Ability copy = a.Copy();
                copy.description = a.description;
                ((CastAbilities) master).addAbility(copy);
            }

        }
    }

    public Ability getAbility(AbilityName a) {
        Ability ability = switch (a) {
            case move -> new Move(a.getId());
            case speed -> new Speed(a.getId());
            case heal -> new Heal(a.getId());
            case buildRock -> new BuildRock(a.getId());
            case buildwraith -> new BuildWraith(a.getId());
            case getBuildHeadlessHorseman -> new BuildHeadlessHorseman(a.getId());
            case buildHeadless->new BuildHeadless(a.getId());
            case buildGreenBarracks -> new BuildGreenBarracks(a.getId());
            case buildMorticum -> new BuildMorticum(a.getId());
            case buildSnek ->new BuildSnek(a.getId());
            case buildBase -> new BuildBase(a.getId());
            case buildTank -> new BuildTank(a.getId());
            case buildBarracks -> new BuildBarracks(a.getId());
            case buildPeasant -> new BuildPeasant(a.getId());
            case buildWhitler -> new BuildWhitler(a.getId());
            case buildWisp -> new BuildWisp(a.getId());
            case buildBoarCavalary -> new BuildBoarCavalary(a.getId());
            default -> null;

        };
        if (ability == null) {
            System.out.println("OOPS WE DON'T HAVE NO SUCH ABILITY TYPE " + a + " HERE");
            return null;
        }
        ability.setName(a.toString());

        return ability;
    }

    public void addAbility(Ability ability) {
        if (ability == null) {
            System.out.println("wtf you tyina add bruv?");
        } else {
            ability.owner = this;
            abilities.add(ability);
            abilities.sort(Comparator.comparingInt(s -> s.id));
        }
    }


    public void addAbility(AbilityName a) {
        Ability Ability = getAbility(a);
        if (Ability == null) {
            System.out.println("wtf you tyina add bruv?");
        } else {
            abilities.add(Ability);
            abilities.sort(Comparator.comparingInt(s -> s.id));
        }
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
                            cast.addAbility(cast.getAbility(name));
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
            if (inde.get() != 0) {
                for (GameObject go : activegameObjects) {
                    CastAbilities cast = go.getComponent(CastAbilities.class);
                    if (cast != null) {
                        cast.removeAbility(name);
                    }
                    guiData.put("guiRemoveAbility", guiData.get("guiRemoveAbility").replace('0' + name, ""));

                }
            }
            this.removeAbility(name);
        }
        return activegameObjects;
    }

    public void removeAbility(String name) {
        for (Ability a : abilities) {
            if (a.name.equals(name)) {
                abilities.remove(a);
                break;
            }
        }

    }

    @Override
    public void updateData(HashMap<String, String> guiData, boolean running) {
        if (!running) {
            StringBuilder alreadyHas = new StringBuilder(guiData.getOrDefault("guiRemoveAbility", ""));

            if (Objects.equals(alreadyHas.toString(), "")) {
                alreadyHas.append("Remove ability");
            }
            List<String> names = new ArrayList<>(List.of(alreadyHas.toString().split(",")));
            for (Ability a : abilities) {
                if (!names.contains(a.name)) {
                    alreadyHas.append('0').append(a.name);
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
