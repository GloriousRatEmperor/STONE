package components;

import SubComponents.*;
import enums.AbilityName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CastAbilities extends Component {
    private float mp=100f;
    private float maxmp=100f;
    public Sprite image;
    public List<Ability> abilities = new ArrayList<>();
    public CastAbilities Clone(){
        return new CastAbilities();
    }
    public CastAbilities(){
    }
    public void castAbility(String AbilityName,Vector2f position){

            for (Ability a : abilities) {
                if (Objects.equals(a.name, AbilityName)) {
                    if(mp>a.mp) {
                        a.cast(position, super.gameObject);
                    }
                }
            }

    }


    public void addAbility(AbilityName a,int index){
        Ability Ability=null;
        switch (a){
            case Move -> 
                Ability=new Move(a,index);
            case Speed -> 
                Ability=new Speed(a,index);
            case Heal -> 
                Ability=new Heal(a,index);
        }
        if(Ability==null){
            System.out.println("OOPS WE DON'T HAVE NO SUCH Ability TYPE HERE");
        }else {
            abilities.add(Ability);
            abilities.sort(Comparator.comparingInt(s -> s.id));
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
                        cast.addAbility(AbilityName.class.getEnumConstants()[index.get()], index.get());
                    }

                }
            }
        };

        return activegameObjects;
    }
}
