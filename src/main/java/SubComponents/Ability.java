package SubComponents;

import components.Sprite;
import enums.abilityName;
import org.joml.Vector2f;

public class Ability {
    public String  name;
    public int id;
    private Sprite sprite;
    public Ability(abilityName a,int id){
        this.name= a.name();
        this.id=id;
    }
    public void Trigger(Vector2f position){

    }
    public Sprite getSprite(){
        return sprite;
    }
}
