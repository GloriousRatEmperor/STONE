package SubComponents.Abilities;

import Multiplayer.ServerData;
import components.CastAbilities;
import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector3f;
import util.Img;
import util.Unit;

public class UnlockAbility extends Ability{
    AbilityName unlockType;
    String unlockName;
    Vector3f unlockCost;
    @Override
    public UnlockAbility clone(){

        System.out.println("there is a use for clone apparently");
        return new UnlockAbility(this.id,unlockName, unlockType);
    }



    public UnlockAbility( int id,String unlockName,AbilityName type) {
        super(id);
        unlockCost= Unit.getUnlockCost(unlockName);
        this.unlockName=unlockName;
        mp=0;
        super.sprite= Img.get(unlockName);
        setDescription("unlocks "+unlockName);
        this.unlockType =type;
    }
    
    @Override
    public void cast(ServerData data, GameObject self){
        CastAbilities cast= self.getComponent(CastAbilities.class);

        cast.addAbility(unlockType);
        cast.removeAbility(name);
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
