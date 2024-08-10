package components;

import Abilitiess.*;
import Multiplayer.ServerData;
import enums.AbilityName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import jade.Window;
import util.AssetPool;

import java.util.*;

public class CastAbilities extends Component {
    private float mp=100f;
    private float maxmp=100f;
    public List<Ability> abilities = new ArrayList<>();
    public transient HashSet<Integer> abilityIds = new HashSet<>();
    public String[] guiRemoveAbility =new String[]{};
    public CastAbilities Clone(){
        CastAbilities ablits=new CastAbilities();
        mengui(ablits);
        return ablits;
    }
    public CastAbilities(){
        updateName();
    }
    public Boolean isCastable(AbilityName type){
        for (Ability a : abilities) {
            if (Objects.equals(a.getType(), type)) {
                return a.Castable(mp)&&gameObject.allied== Window.get().allied;
            }
        }
        return false;
    }
    public void castAbility(ServerData data){
            for (Ability a : abilities) {
                if (Objects.equals(a.name, data.getstrValue())) {
                    if(a.Castable(mp)&&gameObject.allied== Window.get().allied) {
                        a.cast(data, super.gameObject);
                        mp-=a.mp;
                    }
                }
            }

    }
    public void start(){
        for (Ability a : abilities) {
            if (a.sprite.getTexture() != null) {
                a.sprite.setTexture(AssetPool.getTexture(a.sprite.getTexture().getFilepath()));
            }
        }

    }
    @Override
    public void mengui(Component master){
        for (Ability a:
        abilities) {
            if(!((CastAbilities) master).abilityIds.contains(a.id)){
                Ability copy=a.Copy();
                copy.description=a.description;
                ((CastAbilities) master).addAbility(copy);
            }

        }

        ((CastAbilities) master).updateName();
    }

    public Ability getAbility(AbilityName a){
        Ability ability=switch (a){
            case Move ->
                new Move(1);
            case Speed ->
                new Speed(2);
            case Heal ->
                new Heal(3);
            case BuildRock ->
                    new BuildRock(5);
            case BuildBase ->
                    new BuildBase(6);
            case BuildTank ->
                    new BuildTank(7);
            case BuildBarracks ->
                    new BuildBarracks(8);
            case BuildPeasant ->
                    new BuildPeasant(9);
            case BuildWhitler ->
                    new BuildWhitler(10);
            case BuildWisp ->
                    new BuildWisp(11);
            default -> null;

        };
        if(ability==null){
            System.out.println("OOPS WE DON'T HAVE NO SUCH ABILITY TYPE "+a+" HERE");
            return null;
        }
        ability.setName(a.toString());
        ability.setType(a);
        return ability;
    }

    public void addAbility(Ability Ability){
        if(Ability==null){
            System.out.println("wtf you tyina add bruv?");
        }else {
            abilities.add(Ability);
            abilities.sort(Comparator.comparingInt(s -> s.id));
            abilityIds.add( Ability.id);
            updateName();
        }
    }
    public void addAbility(AbilityName a){
        Ability Ability=getAbility(a);
        if(Ability==null){
            System.out.println("wtf you tyina add bruv?");
        }else {
            abilities.add(Ability);
            abilities.sort(Comparator.comparingInt(s -> s.id));
            abilityIds.add( Ability.id);
            updateName();
        }
    }
    @Override
    public List<GameObject> masterGui(List<GameObject> activegameObjects) {
        super.masterGui(activegameObjects);
        String[] enumValues = getEnumValues(AbilityName.class);
        ImInt index=new ImInt(0);


        if (ImGui.combo("Add Ability",index, enumValues, enumValues.length)) {
            if(index.get()!=0) {
                for (GameObject go : activegameObjects) {
                    CastAbilities cast = go.getComponent(CastAbilities.class);
                    if (cast != null) {
                        cast.addAbility(cast.getAbility(AbilityName.class.getEnumConstants()[index.get()]));

                    }

                }
            }
        };
        ImInt inde=new ImInt(0);

        if (ImGui.combo("Remove Ability",inde, guiRemoveAbility, guiRemoveAbility.length)) {
            String name = guiRemoveAbility[inde.get()];
            if(inde.get()!=0) {
                for (GameObject go : activegameObjects) {
                    CastAbilities cast = go.getComponent(CastAbilities.class);
                    if (cast != null) {
                        cast.removeAbility(name);
                    }

                }
            }
            this.removeAbility(name);
        };
        return activegameObjects;
    }
    public void removeAbility(String name){
        for (Ability a:abilities){
            if(a.name.equals(name)){
                abilities.remove(a);
                abilityIds.remove(a.id);
                updateName();
                break;
            }
        }

    }

    private void updateName(){
        List<String> names = new ArrayList<>();
        names.add("Remove ability");
        for (Ability a :abilities){
            names.add(a.name);

        }
        guiRemoveAbility = new String[names.size()];
        guiRemoveAbility = names.toArray(guiRemoveAbility);
    }
    @Override

    public void destroy(){
        this.abilityIds.clear();
        this.abilities.clear();
    }
}
