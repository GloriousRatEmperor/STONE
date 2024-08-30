package SubComponents.Abilities;

import util.Img;

public class BuildBarracks extends SubComponents.Abilities.BuildBuilding {
    @Override
    public BuildBarracks Copy(){
        BuildBarracks buildBarracks=new BuildBarracks(id);
        return  buildBarracks;
    }
    public BuildBarracks( int id) {
        super(id,"barracks");
        setDesc("Builds a Barracks");
        BuildName="Barracks";
        super.sprite= Img.get("barracks");
    }
}
