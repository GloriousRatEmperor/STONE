package SubComponents.Abilities;

import Multiplayer.ServerData;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Img;
import util.Unit;

import static jade.Window.getScene;

public class BuildBuilding extends Ability {


    public Vector3f cost=new Vector3f(0,0,0);
    public String buildName;
    @Override
    public BuildBuilding Copy(){
        BuildBuilding buildBuilding=new BuildBuilding(type);
        return buildBuilding;
    }
    @Override
    public void cast(ServerData data, GameObject self){
        if(getScene().addmoney(-cost.x,-cost.y,-cost.z,self.allied)) {
            Vector2f position = new Vector2f(data.position.get(0), data.position.get(1));
            GameObject unit = Unit.makeBuilding(buildName, position, self.allied);
            getScene().addGameObjectToScene(unit);
        }
    }
    public BuildBuilding(AbilityName type,String name) {
        super( type);
        buildName=name;
        cost=Unit.getBuildCost(name);
        setDesc( type.getDesc());
        targetable=true;
        mp=0;
        super.sprite= Img.get(name);
    }
    public BuildBuilding(AbilityName type) {
        super( type);

        setDesc( type.getDesc());
        targetable=true;
        mp=0;
    }
    @Override
    public void setDesc(String description) {
        String bldcost="",rockcost="",magecost="";
        if(cost.x!=0){
             bldcost=cost.x+ " Blood";
        }
        if(cost.y!=0){
             rockcost=cost.y+ " Rock";
        }
        if(cost.z!=0){
             magecost=cost.z+ " Magic";
        }
        if(!bldcost.equals("") || !magecost.equals("")|| !rockcost.equals("")){
            this.description= description+"|4 costs"+bldcost+rockcost+magecost;
        }else{
            this.description= description;
        }
    }
}
