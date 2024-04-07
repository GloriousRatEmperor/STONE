package components;

import Multiplayer.ServerData;
import Abilitiess.*;
import enums.AbilityName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class CastAbilities extends Component {
    private float mp=100f;
    private float maxmp=100f;
    public List<Ability> abilities = new ArrayList<>();
    public HashSet<Integer> abilityIds = new HashSet<>();
    public String[] guiRemoveAbility =new String[]{};
    public CastAbilities Clone(){
        CastAbilities ablits=new CastAbilities();
        return ablits;
    }
    public CastAbilities(){
        updateName();
    }
    public Boolean isCastable(String name){
        for (Ability a : abilities) {
            if (Objects.equals(a.name, name)) {
                return a.Castable(mp);
            }
        }
        return false;
    }
    public void castAbility(ServerData data){
            for (Ability a : abilities) {
                if (Objects.equals(a.name, data.name)) {
                    if(a.Castable(mp)) {
                        a.cast(data, super.gameObject);
                        mp-=a.mp;
                    }
                }
            }

    }
    public void start(){
        Ability PropelyDeserialized;
        List<Ability> corrpuptables= new ArrayList<>(abilities);
        for (Ability a :
             corrpuptables) {
            PropelyDeserialized=getAbility(a.name);

            Field[] fields = a.getClass().getDeclaredFields();
            try{
                for (Field field : fields) {
                    boolean isTransient = Modifier.isTransient(field.getModifiers());
                    if (isTransient) {
                        continue;
                    }

                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                    if (isPrivate) {
                        field.setAccessible(true);
                    }
                    Object value = field.get(a);
                    field.set(PropelyDeserialized, value);
                    if (isPrivate) {
                        field.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }

            addAbility(PropelyDeserialized);
            abilities.remove(a);
        }

    }
    @Override
    public void mengui(Component master){
        for (Ability a:
        abilities) {
            if(!((CastAbilities) master).abilityIds.contains(a.id)){
                ((CastAbilities) master).addAbility(a.Copy());
            }

        }
        ((CastAbilities) master).updateName();
    }

    public Ability getAbility(String a){
        Ability Ability=null;
        switch (a){
            case "Move" ->
                Ability=new Move(a,1);
            case "Speed" ->
                Ability=new Speed(a,2);
            case "Heal" ->
                Ability=new Heal(a,3);
            case "BuildUnit" ->
                Ability=new BuildUnit(a,4);

        }
        if(Ability==null){
            System.out.println("OOPS WE DON'T HAVE NO SUCH ABILITY TYPE "+a+" HERE");

        }
        return Ability;
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
                        cast.addAbility(cast.getAbility(AbilityName.class.getEnumConstants()[index.get()].name()));

                    }

                }
            }
        };
        ImInt inde=new ImInt(0);

        if (ImGui.combo("Remove Ability",inde, guiRemoveAbility, guiRemoveAbility.length)) {
            if(inde.get()!=0) {
                for (GameObject go : activegameObjects) {
                    CastAbilities cast = go.getComponent(CastAbilities.class);
                    if (cast != null) {
                        cast.removeAbility(guiRemoveAbility[inde.get()]);
                    }

                }
            }
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
