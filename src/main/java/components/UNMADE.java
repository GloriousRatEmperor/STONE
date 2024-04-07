package components;

import org.joml.Vector3i;
import util.Unit;
public class UNMADE {
    public String name;
    public float time;
    public UNMADE(String name) {
        this.name=name;
        this.time=Unit.getBuildTime(name);
    }
}
