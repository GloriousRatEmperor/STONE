package SubComponents.Abilities;

public class BuildWhitler extends BuildUnit{
    @Override
    public BuildWhitler Copy(){

        BuildWhitler buildUnit=new BuildWhitler(id);
        return buildUnit;
    }
    public BuildWhitler(int id) {
        super(id,"Whitler");
        setDescription("Builds a Whitler, these are unbiased workers that can harness minerals of any kind");
    }
}
