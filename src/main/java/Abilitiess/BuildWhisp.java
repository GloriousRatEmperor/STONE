package Abilitiess;

import org.joml.Vector3f;

public class BuildWhisp extends BuildUnit{
    @Override
    public BuildWhisp Copy(){

        BuildWhisp buildUnit=new BuildWhisp(id);
        return buildUnit;
    }
    public BuildWhisp(int id) {
        super(id);
        super.UnitName="Whisp";
        super.cost=new Vector3f(0,0,50);
        setDescription("Builds a Whisp");
    }
}
