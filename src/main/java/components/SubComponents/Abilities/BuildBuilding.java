package components.SubComponents.Abilities;

import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Img;
import util.UnitUtils.BuildingCreator;

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
    public boolean cast(final Vector2f pos,GameObject self,GameObject target){
        if(getScene().addmoney(-cost.x,-cost.y,-cost.z,self.allied)) {

            GameObject unit = BuildingCreator.makeBuilding(buildName, new Vector2f(pos), self.allied);
            getScene().addGameObjectToScene(unit);
            return true;
        }else{
            return false;
        }
    }
    public BuildBuilding(AbilityName type,String name) {
        super( type);
        range=0.1f;
        buildName=name;
        cost=BuildingCreator.getBuildCost(name);
        setDesc( type.getDesc());
        targetable=true;
        mp=0;
        super.sprite= Img.get(name);
    }
    public BuildBuilding(AbilityName type) {
        super( type);
        range=0.1f;
        setDesc( type.getDesc());
        targetable=true;
        mp=0;
    }
    public boolean Castable(float MP){
        return getScene().haveMoney(cost.x,cost.y,cost.z,owner.gameObject.allied);
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
    @Override
    public boolean consider(GameObject self, boolean mpcapped){
        return true;
    }
}
