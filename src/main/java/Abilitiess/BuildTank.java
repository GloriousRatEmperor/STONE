package Abilitiess;

import org.joml.Vector3f;

public class BuildTank extends BuildUnit{
    @Override
    public BuildTank Copy(){

        BuildTank buildUnit=new BuildTank(id);
        return buildUnit;
    }
    public BuildTank( int id) {
        super(id);
        super.UnitName="Tank";
        super.cost=new Vector3f(0,75,0);
        setDescription("Builds a tanky rock");
    }
}
