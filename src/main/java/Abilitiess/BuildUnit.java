package Abilitiess;

import Multiplayer.ServerData;
import components.UnitBuilder;
import jade.GameObject;
import org.joml.Vector3f;
import util.Img;

public class BuildUnit extends Ability{
    public Vector3f cost;
    public String UnitName;
    public BuildUnit Copy(){
        BuildUnit buildUnit=new BuildUnit(this.name,id);
        return buildUnit;
    }
    public BuildUnit(String a, int id) {
        super(a, id);
        mp=0;
        sprite= Img.get("build");

    }
    @Override
    public void cast(ServerData data, GameObject self){
        System.out.println(data);
        self.getComponent(UnitBuilder.class).addqueue(UnitName,cost);
    }


}
