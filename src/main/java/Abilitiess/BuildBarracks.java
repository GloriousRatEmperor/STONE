package Abilitiess;

import util.Img;

public class BuildBarracks extends BuildBuilding {
    @Override
    public BuildBarracks Copy(){
        BuildBarracks buildBarracks=new BuildBarracks(id);
        return  buildBarracks;
    }
    public BuildBarracks( int id) {
        super(id,"rekrootment");
        setDesc("Builds a Barracks");
        BuildName="Barracks";
        super.sprite= Img.get("rekrootment");
    }
}
