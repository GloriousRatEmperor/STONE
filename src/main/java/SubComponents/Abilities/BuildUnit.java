package SubComponents.Abilities;

import Multiplayer.ServerData;
import components.UnitBuilder;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector3f;
import util.Img;
import util.Unit;

public class BuildUnit extends Ability{
    public Vector3f cost;
    public String unitName;
    @Override
    public BuildUnit Copy(){
        BuildUnit buildUnit=new BuildUnit(type,this.unitName);
        return buildUnit;
    }
    public BuildUnit(AbilityName type, String unitName) {
        super(type);
        this.unitName =unitName;
        cost= Unit.getCost(unitName);

        mp=0;
        super.sprite= Img.get(this.unitName);
        setDescription(type.getDesc());
    }
    @Override
    public void cast(ServerData data, GameObject self){
        UnitBuilder build= self.getComponent(UnitBuilder.class);
        if (build!=null){
            build.addqueue(unitName,cost);
        }
    }
    public void setDescription(String description) {
        String bldcost="",rockcost="",magecost="";
        if(cost.x!=0){
            bldcost=cost.x+ " Blood ";
        }
        if(cost.y!=0){
            rockcost=cost.y+ " Rock ";
        }
        if(cost.z!=0){
            magecost=cost.z+ " Magic ";
        }
        if(!bldcost.equals("")||!magecost.equals("")||!rockcost.equals("")) {
            this.description = description + "|2 costs: " +bldcost+rockcost+magecost;
        }else{
            this.description = description;
        }
    }


}
