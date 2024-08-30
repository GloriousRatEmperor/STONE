package SubComponents.Abilities;

public class BuildTank extends BuildUnit{
    @Override
    public BuildTank Copy(){

        BuildTank buildUnit=new BuildTank(id);
        return buildUnit;
    }
    public BuildTank( int id) {
        super(id,"Tank");
        setDescription("Builds a tanky rock");
    }
}
