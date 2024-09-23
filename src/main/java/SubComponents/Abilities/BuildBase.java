package SubComponents.Abilities;

import util.Img;
import util.Unit;

public class BuildBase extends SubComponents.Abilities.BuildBuilding {
    private int race=4;//1->blood 2->rock 3->magic 4->white
    private int startID;
    @Override
    public BuildBase Copy(){
        BuildBase buildBase=new BuildBase(startID);
        buildBase.setRace(race);
        return  buildBase;
    }
    public BuildBase( int id) {
        super(id);
        startID=id;

    }
    @Override
    public void start(){
        startRace(race);
    }
    public void startRace(int race){
        this.race=race;
        this.id=startID-1+race;
        switch (race){
            case 1-> {
                this.cost = Unit.getBuildCost("bloodBase");
                setDesc("Builds a bloody base");
                BuildName = "bloodbase";
                super.sprite = Img.get("bloodbase");
            }
            case 2-> {
                this.cost =  Unit.getBuildCost("rockbase");
                setDesc("Builds a rock base");
                BuildName = "rockbase";
                super.sprite = Img.get("rockbase");
            }
                case 3-> {
                    this.cost =Unit.getBuildCost("magicbase");
                    setDesc("Builds a magic base");
                    BuildName = "magicbase";
                    super.sprite = Img.get("magicbase");
                }
            case 4-> {
                this.cost = Unit.getBuildCost("whitebase");
                setDesc("Builds a balanced base");
                BuildName ="whitebase";
                super.sprite = Img.get("whitebase");
            }
        }
    }
    public void setRace(int race){
        race=race;
    }
    public int getRace(){
        return race;
    }

}
