package enums;

public enum EffectName {
    Select_Effect(0),
    speedUp(2),
    explodeOnDeath(1),
    explodingProjectiles(2);


    private final int id;
    EffectName(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }

}
