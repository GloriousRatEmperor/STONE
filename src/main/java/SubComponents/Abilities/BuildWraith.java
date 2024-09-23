package SubComponents.Abilities;

public class BuildWraith extends BuildUnit{


    @Override
    public BuildWraith Copy(){

        BuildWraith buildUnit=new BuildWraith(id);
        return buildUnit;
    }
    public BuildWraith( int id) {
        super(id,"wraith");

        setDescription("Builds a wraith");

    }

}
