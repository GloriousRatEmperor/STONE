package enums;

public enum EffectName {
    Select_Effect(-1),
    speedUp(2),
    explodeOnDeath(1),
    explodingProjectiles(3),
    timedLife(4),
    FearProjectiles(5);

    private final int id;
    EffectName(int id){
        this.id=id*100;
    }
    public int getId(){
        return id;
    }

}
