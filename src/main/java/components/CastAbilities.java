package components;

import SubComponents.Ability;
import enums.abilityName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CastAbilities extends Component {
    private Vector2f mp=new Vector2f(100f,100f);
    public Sprite image;
    public List<Ability> abilities = new ArrayList<>();
    public CastAbilities Clone(){
        return new CastAbilities();
    }
    public CastAbilities(){
    }
    public void castAbility(String abilityName,Vector2f position){
        for(Ability a:abilities){
            if(Objects.equals(a.name, abilityName)){
                a.Trigger(position);
            }
        }
    }
    public void addAbility(abilityName a,int index){
        abilities.add(new Ability(a,index));
        abilities.sort(Comparator.comparingInt(s -> s.id));
    }
    @Override
    public List<GameObject> masterGui(List<GameObject> activegameObjects) {
        String[] enumValues = getEnumValues(abilityName.class);
        ImInt index=new ImInt(0);


        if (ImGui.combo("Add ability",index, enumValues, enumValues.length)) {
            if(index.get()!=0) {
                for (GameObject go : activegameObjects) {
                    CastAbilities cast = go.getComponent(CastAbilities.class);
                    if (cast != null) {
                        cast.addAbility(abilityName.class.getEnumConstants()[index.get()], index.get());
                    }

                }
            }
        };

        return activegameObjects;
    }
}
