package SubComponents.Abilities;

public class BuildBoarCavalary extends BuildUnit{
    @Override
    public BuildPeasant Copy(){

        BuildPeasant buildUnit=new BuildPeasant(id);
        return buildUnit;
    }
    public BuildBoarCavalary(int id) {
        super(id,"BoarCavalary");
        setDescription("Builds a Boar cavalary");
    }
}
