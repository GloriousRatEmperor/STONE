package SubComponents.Abilities;

public class BuildPriest extends BuildUnit{
    @Override
    public BuildPriest Copy(){

        BuildPriest buildUnit=new BuildPriest(id);
        return buildUnit;
    }
    public BuildPriest( int id) {
        super(id,"priest");

        setDescription("Builds a priest");

    }
}
