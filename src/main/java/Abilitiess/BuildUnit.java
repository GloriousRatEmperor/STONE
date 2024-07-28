package Abilitiess;

import Multiplayer.ServerData;
import components.UnitBuilder;
import jade.GameObject;
import org.joml.Vector3f;
import util.Img;

public class BuildUnit extends Ability{
    public Vector3f cost=new Vector3f(0,0,0);
    public String UnitName;
    @Override
    public BuildUnit Copy(){
        BuildUnit buildUnit=new BuildUnit(id);
        return buildUnit;
    }
    public BuildUnit( int id) {
        super(id);
        mp=0;
        super.sprite= Img.get("build");
        setDescription("Builds a ??UNIT??");

    }
    @Override
    public void cast(ServerData data, GameObject self){
        UnitBuilder build= self.getComponent(UnitBuilder.class);
        if (build!=null){
            build.addqueue(UnitName,cost);
        }
    }
    public void setDescription(String description) {
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
            this.description= description+" costs";
        }
    }


}
