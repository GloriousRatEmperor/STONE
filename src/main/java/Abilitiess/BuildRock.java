package Abilitiess;

public class BuildRock extends BuildUnit{
    @Override
    public BuildRock Copy(){

        BuildRock buildUnit=new BuildRock(id);
        return buildUnit;
    }
    public BuildRock(int id) {
        super(id,"Rock");
        setDescription("Builds a Rock");
    }
}
