package SubComponents.Abilities;

public class BuildWisp extends BuildUnit{
    @Override
    public BuildWisp Copy(){

        BuildWisp buildUnit=new BuildWisp(id);
        return buildUnit;
    }
    public BuildWisp(int id) {
        super(id,"Wisp");
        setDescription("Builds a Wisp, a fragile magic worker with a decent attack");
    }
}
