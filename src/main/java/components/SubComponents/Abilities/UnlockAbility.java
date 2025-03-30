package components.SubComponents.Abilities;

import components.unitcapabilities.defaults.CastAbilities;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Img;
import util.UnitUtils.UnlockTracker;

public class UnlockAbility extends Ability{
    AbilityName unlockType;
    String unlockName;
    Vector3f unlockCost;



    public UnlockAbility(AbilityName type,String unlockName) {
        super(type);
        unlockCost= UnlockTracker.getUnlockCost(unlockName);
        this.unlockName=unlockName;
        mp=0;
        super.sprite= Img.get(unlockName);
        setDescription("unlocks "+unlockName);
        this.unlockType =type;
    }
    
    @Override
    public boolean cast(final Vector2f pos, GameObject self,GameObject target){
        CastAbilities cast= self.getComponent(CastAbilities.class);

        cast.addAbility(unlockType);
        cast.removeAbility(getID());
        return true;
    }
    public void setDescription(String description) {
        String bldcost="",rockcost="",magecost="";
        if(unlockCost.x!=0){
            bldcost=unlockCost.x+ " Blood ";
        }
        if(unlockCost.y!=0){
            rockcost=unlockCost.y+ " Rock ";
        }
        if(unlockCost.z!=0){
            magecost=unlockCost.z+ " Magic ";
        }
        if(!bldcost.equals("")||!magecost.equals("")||!rockcost.equals("")) {
            this.description = description + "|2 costs: " +bldcost+rockcost+magecost;
        }else{
            this.description = description;
        }
    }




}
