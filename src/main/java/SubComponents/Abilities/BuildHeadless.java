package SubComponents.Abilities;

public class BuildHeadless extends BuildUnit{


    @Override
    public BuildHeadless Copy(){

        BuildHeadless buildUnit=new BuildHeadless(id);
        return buildUnit;
    }
    public BuildHeadless( int id) {
        super(id,"headless");
        setDescription("Builds a headless undead sworsman");

    }
}
