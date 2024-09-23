package SubComponents.Abilities;

public class BuildHeadlessHorseman extends BuildUnit{
    @Override
    public BuildHeadlessHorseman Copy(){

        BuildHeadlessHorseman buildUnit=new BuildHeadlessHorseman(id);
        return buildUnit;
    }
    public BuildHeadlessHorseman( int id) {
        super(id,"HeadlessHorseman");
        setDescription("Builds a headless undead horseman");

    }
}
