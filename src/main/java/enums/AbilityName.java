package enums;

public enum AbilityName {

    Select_Ability(-1,""),
    move(0,"issues a move command"),
    heal(1,"restores this unit's hp"), //these descriptions may not necessarily show up, that depends on the specific ability
    speed(2, "increases this unit's move speed"),
    buildBase(3, "builds a base"),
    buildBaseR(4,"builds a bloody base"),
    buildBaseG(5,"builds a rock base"),
    buildBaseB(6,"builds a magic base"),
    buildBaseA(7,"builds a white base"),
    buildBarracks(22, "builds a barracs"),
    buildTank(8, "builds a tank"),
    buildBoarCavalary(9, "Builds a Boar cavalary"),
    buildWisp(10, "Builds a Wisp, a fragile magic worker with a decent attack (compared to other workers)"),
    buildPeasant(12,"Builds a Peasant, fleshy lil worker"),
    buildWhitler(13,"Builds a Whitler, basic workers"),
    buildRock(14,"Builds a Rock, the standard rock worker"),
    buildPriest(23,"Builds a priest"),
    buildHeadless(18,"Builds a headless undead sworsman"),
    buildwraith(17,"Builds a wraith"),
    buildSnek(21,"Builds a fire serpent"),
    getBuildHeadlessHorseman(16,"Builds a headless undead horseman"),
    buildGreenBarracks(15,"Builds a Barracks that creates meaty units"),

    buildMorticum(20,"Builds a morticum that creates magical undead units"),
    buildChicken(25,"builds a ruthless chicken that knows no mercy"),
    buildPebble(26,"builds a smol rock"),
    buildSpearman(24,"Builds a burly spearman. spearmen do additional damage to enemies charging at them"),
    guardMode(27,"toggles guardmode");

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
