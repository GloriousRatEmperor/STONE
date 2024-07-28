package components;

import Abilitiess.BuildBuilding;
import org.joml.Vector3f;
import util.Img;

public class BuildBarracks extends BuildBuilding {
    @Override
    public BuildBarracks Copy(){
        BuildBarracks buildBarracks=new BuildBarracks(id);
        return  buildBarracks;
    }
    public BuildBarracks( int id) {
        super(id);
        this.cost=new Vector3f(0,500,0);
        description="Builds a Barracks";
        BuildName="Barracks";
        super.sprite= Img.get("rekrootment");
    }
}
