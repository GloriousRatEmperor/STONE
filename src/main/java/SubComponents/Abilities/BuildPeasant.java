package SubComponents.Abilities;

public class BuildPeasant extends BuildUnit{
    @Override
    public BuildPeasant Copy(){

        BuildPeasant buildUnit=new BuildPeasant(id);
        return buildUnit;
    }
    public BuildPeasant(int id) {
        super(id,"Peasant");
        setDescription("Builds a Peasant, fleshy lil worker");
    }
}
