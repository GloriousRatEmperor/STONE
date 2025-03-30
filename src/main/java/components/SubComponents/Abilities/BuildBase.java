package components.SubComponents.Abilities;

import enums.AbilityName;
import util.Img;
import util.UnitUtils.BuildingCreator;

import static enums.AbilityName.*;

public class BuildBase extends BuildBuilding {
    private int race=4;//1->blood 2->rock 3->magic 4->white
    @Override
    public BuildBase Copy(){
        BuildBase buildBase=new BuildBase(type);
        buildBase.race=race;
        return  buildBase;
    }
    public BuildBase(AbilityName type) {
        super(type);
    }
    public BuildBase(AbilityName type,int race) {
        super(type);
        setRace(race);
    }
    @Override
    public void start(){
        startRace(race);
    }
    public void startRace(int race){
        this.race=race;
        switch (race){
            case 1-> {
                this.type=buildBaseR;
                this.cost = BuildingCreator.getBuildCost("bloodBase");
                setDesc("Builds a bloody base");
                buildName = "bloodbase";
                super.sprite = Img.get("bloodbase");
            }
            case 2-> {
                this.type=buildBaseG;
                this.cost =  BuildingCreator.getBuildCost("rockbase");
                setDesc("Builds a rock base");
                buildName = "rockbase";
                super.sprite = Img.get("rockbase");
            }
                case 3-> {
                    this.type=buildBaseB;
                    this.cost =BuildingCreator.getBuildCost("magicbase");
                    setDesc("Builds a magic base");
                    buildName = "magicbase";
                    super.sprite = Img.get("magicbase");
                }
            case 4-> {
                this.type=buildBaseA;
                this.cost = BuildingCreator.getBuildCost("whitebase");
                setDesc("Builds a balanced base");
                buildName ="whitebase";
                super.sprite = Img.get("whitebase");
            }
        }
    }
    public void setRace(int race){
        this.race=race;
    }
    public int getRace(){
        return race;
    }

}
