package Abilitiess;

import org.joml.Vector3f;

public class BuildRock extends BuildUnit{
    @Override
    public BuildRock Copy(){

        BuildRock buildUnit=new BuildRock(id);
        return buildUnit;
    }
    public BuildRock(int id) {
        super(id);
        super.UnitName="Rock";
        super.cost=new Vector3f(0,50,0);
        setDescription("Builds a Rock");
    }
}
