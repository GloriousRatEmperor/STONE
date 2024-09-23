package SubComponents.Abilities;

import util.Img;

public class BuildGreenBarracks extends BuildBuilding{
    @Override
    public BuildBarracks Copy(){
        BuildBarracks buildBarracks=new BuildBarracks(id);
        return  buildBarracks;
    }
    public BuildGreenBarracks( int id) {
        super(id,"greenbarracks");
        setDesc("Builds a Barracks that creates meaty units");
        BuildName="GreenBarracks";
        super.sprite= Img.get("greenbarracks");
    }
}
