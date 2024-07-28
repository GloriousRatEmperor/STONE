package Abilitiess;

import org.joml.Vector3f;
import util.Img;

public class BuildBase extends BuildBuilding {
    public int race=1;//1->blood 2->rock 3->magic
    @Override
    public BuildBase Copy(){
        BuildBase buildBase=new BuildBase(id);
        return  buildBase;
    }
    public BuildBase( int id) {
        super(id);
        this.setRace(2);

    }
    public void setRace(int race){
        this.race=race;
        switch (race){
            case 1:
                this.cost=new Vector3f(500,0,0);
                setDesc("Builds a bloody base");
                BuildName="Base1";
                super.sprite= Img.get("bloodBase");
            case 2:
                this.cost=new Vector3f(0,500,0);
                setDesc("Builds a rock base");
                BuildName="Base2";
                super.sprite= Img.get("rockbase");
            case 3:
                this.cost=new Vector3f(0,0,500);
                setDesc("Builds a magic base");
                BuildName="Base3";
                super.sprite= Img.get("magicbase");
            case 4:
                this.cost=new Vector3f(150,150,150);
                setDesc("Builds a balanced base");
                BuildName="Base4";
                super.sprite= Img.get("whitebase");
        }
    }


}
