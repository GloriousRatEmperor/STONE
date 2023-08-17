package physics2d.components;

import components.Component;
import org.joml.Vector2f;

public class MoveContollable extends Component {
    public MoveContollable Clone(){
        return new MoveContollable();
    }
    public int speed=10;

}
