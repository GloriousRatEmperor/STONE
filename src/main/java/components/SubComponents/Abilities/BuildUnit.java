package components.SubComponents.Abilities;

import components.unitcapabilities.UnitBuilder;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Img;
import util.UnitUtils.UnitCreator;

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
        cost= UnitCreator.getUnitCost(unitName);

        mp=0;
        super.sprite= Img.get(this.unitName);
        setDesc(type.getDesc());
    }
    @Override
    public boolean cast(final Vector2f pos, GameObject self,GameObject target){
        UnitBuilder build= self.getComponent(UnitBuilder.class);
        if (build!=null){
            build.addqueue(unitName,cost);
            return true;
        }else{
            return false;
        }
    }
    @Override
    public void setDesc(String description) {
        //adds a colored list in the form of a string of the basic stats like hp, armor etc and at the end a black slab of all the other things the mob has
        String unitStats= UnitCreator.getUStats(unitName.toLowerCase());
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
            this.description = description + "|2 costs: " +bldcost+rockcost+magecost+unitStats;
        }else{
            this.description = description;
        }
    }


}
