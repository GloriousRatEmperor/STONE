package SubComponents.Abilities;

import util.Img;

public class BuildMorticum extends BuildBuilding {
    @Override
    public BuildMorticum Copy() {
        BuildMorticum buildMorticum = new BuildMorticum(id);
        return buildMorticum;
    }

    public BuildMorticum(int id) {
        super(id, "morticum");
        setDesc("Builds a morticum that creates undead units");
        BuildName = "morticum";
        super.sprite = Img.get("morticum");
    }
}