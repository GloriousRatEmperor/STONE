package components;

import SubComponents.*;
import enums.AbilityName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import org.joml.Vector2f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.PublicKey;
import java.util.*;

public class CastAbilities extends Component {
    private float mp=100f;
    private float maxmp=100f;
    public Sprite image;
    public List<Ability> abilities = new ArrayList<>();
    public HashSet<Integer> abilityIds = new HashSet<>();

    public CastAbilities Clone(){
        CastAbilities ablits=new CastAbilities();
        return ablits;
    }
    public CastAbilities(){
    }
    public void castAbility(String AbilityName,Vector2f position){
            for (Ability a : abilities) {
                if (Objects.equals(a.name, AbilityName)) {
                    if(a.Castable(mp)) {
                        a.cast(position, super.gameObject);
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
            PropelyDeserialized=getAbility(a.name,a.id);

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

    }

    public Ability getAbility(String a,int index){
        Ability Ability=null;
        switch (a){
            case "Move" ->
                Ability=new Move(a,index);
            case "Speed" ->
                Ability=new Speed(a,index);
            case "Heal" ->
                Ability=new Heal(a,index);
        }
        if(Ability==null){
            System.out.println("OOPS WE DON'T HAVE NO SUCH Ability TYPE HERE");

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
                        cast.addAbility(cast.getAbility(AbilityName.class.getEnumConstants()[index.get()].name(), index.get()));
                    }

                }
            }
        };

        return activegameObjects;
    }
    @Override

    public void destroy(){
        this.abilityIds.clear();
        this.abilities.clear();
    }
}
