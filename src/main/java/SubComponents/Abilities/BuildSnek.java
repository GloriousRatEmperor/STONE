package SubComponents.Abilities;

public class BuildSnek extends BuildUnit{


    @Override
    public BuildSnek Copy(){

        BuildSnek buildUnit=new BuildSnek(id);
        return buildUnit;
    }
    public BuildSnek( int id) {
        super(id,"snek");

        setDescription("Builds a fire serpent");

    }
}
