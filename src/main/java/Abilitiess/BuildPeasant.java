package Abilitiess;

import org.joml.Vector3f;

public class BuildPeasant extends BuildUnit{
    @Override
    public BuildPeasant Copy(){

        BuildPeasant buildUnit=new BuildPeasant(id);
        return buildUnit;
    }
    public BuildPeasant(int id) {
        super(id);
        super.UnitName="Peasant";
        super.cost=new Vector3f(50,0,0);
        setDescription("Builds a Peasant");
    }
}
