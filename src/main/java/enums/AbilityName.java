package enums;

public enum AbilityName {
    //0-100 is a regular ability, 100+ is build building, 900+ is build unit
    Select_Ability(-1,""),
    move(0,"issues a move command"),
    heal(1,"restores this unit's hp"), //these descriptions may not necessarily show up, that depends on the specific ability
    speed(2, "increases this unit's move speed"),
    buildBase(101, "builds a base"),
    buildBaseR(102,"builds a bloody base"),
    buildBaseG(103,"builds a rock base"),
    buildBaseB(104,"builds a magic base"),
    buildBaseA(105,"builds a white base"),
    buildBarracks(106, "builds a barracs"),
    buildTank(905, "builds a tank"),
    buildBoarCavalary(906, "Builds a Boar cavalary"),
    buildWisp(901, "Builds a Wisp, a fragile magic worker with a decent attack (compared to other workers)"),
    buildPeasant(902,"Builds a Peasant, fleshy lil worker"),
    buildWhitler(900,"Builds a Whitler, basic workers"),
    buildRock(903,"Builds a Rock, the standard rock worker"),
    buildPriest(904,"Builds a priest"),
    buildHeadless(907,"Builds a headless undead sworsman"),
    buildwraith(908,"Builds a wraith"),
    buildSnek(909,"Builds a fire serpent"),
    getBuildHeadlessHorseman(910,"Builds a headless undead horseman"),
    buildGreenBarracks(107,"Builds a Barracks that creates meaty units"),

    buildMorticum(108,"Builds a morticum that creates magical undead units"),
    buildChicken(911,"builds a ruthless chicken that knows no mercy"),
    buildPebble(912,"builds a smol rock"),
    buildSpearman(913,"Builds a burly spearman. spearmen do additional damage to enemies charging at them"),
    guardMode(99,"toggles guardmode"),
    buildSpider(914,"Builds a spider charriot (artillery unit)"),
    buildStoneborn(915,"Builds a stoneborn"),
    buildVolcano(916,"Builds a volcano"),
    buildTotem(999,"Builds a drowned totem, which is like kind of not really a super intended to be hype unit or anything but here it... exists I guess? mb will get update"),
    errupt(98,"ERRUPTION, a deathrattle that murders nearby stuff"),
    buildAlterator(917,"Builds an alternator, a powerful spellcaster that marks enemy units"),
    shootAlterBolt(10,"shoots an alterbolt"),
    teleportMarked(11,"tp to a marked enemy"),
    buildBuffBird(917,"this is no ordinary bird, it used magical prowess to mold itself into the perfect weapon"),
    Alterate(12,"does nothing yet!");

    private final int id;
    private final String desc;
    AbilityName(int id, String description){
        this.desc=description;
        this.id=id;
    }
    public int getId(){
        return id;
    }
    public String getDesc(){
        return desc;
    }
    public static AbilityName StringToAbilityName(String name) {
        for (AbilityName a : AbilityName.class.getEnumConstants()) {
            if (a.name().equals(name)) {
                return a;
            }

        }
        return null;
    }
}
