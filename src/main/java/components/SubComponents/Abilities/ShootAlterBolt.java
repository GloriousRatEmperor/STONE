package components.SubComponents.Abilities;

import enums.AbilityName;
import util.Img;
import util.UnitUtils.ProjectileCreator;

public class ShootAlterBolt extends ShootProjectile{

    public ShootAlterBolt(AbilityName type) {
        super(type);
        sprite= Img.get("alteratormark");
        projectileName="alterbolt";
        updateDesc();
        this.range= ProjectileCreator.calcRange("alterbolt");
    }


    @Override
    public void updateDesc(){
        setDesc("shoots a bolt of mana at selected location, marks units hit");
    }


}
