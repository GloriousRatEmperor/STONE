package enums;

public enum AbilityName {

    Select_Ability(-1),
    move(0),
    heal(1),
    speed(2),
    buildBase(3),//is actually 3+0 for blooodbase, 3+1 for greenbase ... 3+3 for whitebase so nuffin else can have id 3-6
    buildBarracks(7),
    buildTank(8),
    buildBoarCavalary(9),
    buildWisp(10),
    buildPeasant(12),
    buildWhitler(13),
    buildRock(14),
    buildHeadless(18),
    buildwraith(17),
    buildSnek(21),
    getBuildHeadlessHorseman(16),
    buildGreenBarracks(15),

    buildMorticum(20);

    private final int id;
    AbilityName(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }
}
