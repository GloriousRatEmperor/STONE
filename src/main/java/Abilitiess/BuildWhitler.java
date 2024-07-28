package Abilitiess;

import org.joml.Vector3f;

public class BuildWhitler extends BuildUnit{
    @Override
    public BuildWhitler Copy(){

        BuildWhitler buildUnit=new BuildWhitler(id);
        return buildUnit;
    }
    public BuildWhitler(int id) {
        super(id);
        super.UnitName="Whitler";
        super.cost=new Vector3f(20,20,20);
        setDescription("Builds a Whitler");
    }
}
