package util.UnitUtils;

import org.joml.Vector3f;

import java.util.HashMap;

public class UnlockTracker {


    static public HashMap<String,Float> unlockCosts = new HashMap<String, Float>();

    public static Vector3f getUnlockCost(String name){
        name=name.toLowerCase();

        return new Vector3f( unlockCosts.getOrDefault(name+"."+"costblood",0f)
                ,unlockCosts.getOrDefault(name+"."+"costrock",0f)
                ,unlockCosts.getOrDefault(name+"."+"costmagic",0f));
    }


}
