package physics2d.components;

import components.Component;
import org.joml.Vector2f;

public class MoveContollable extends Component {
    public MoveContollable Clone(){
        MoveContollable c=new MoveContollable();
        c.speed=speed;
        return c;
    }
    public int speed=3;

}
