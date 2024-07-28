package components;

import static jade.Window.getScene;

public class Variables extends Component {
    public int startBlood = 0;
    public int startRock = 100;
    public int startMagic = 0;

    @Override
    public void start() {

        getScene().setMoney(startBlood, startRock, startMagic);
    }
}