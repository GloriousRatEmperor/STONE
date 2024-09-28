package util;

import static jade.Window.getScene;

public class Variables {
    public static int startBlood = 500000;
    public static int startRock = 500000;
    public static int startMagic = 500000;


    public static void start() {

        getScene().setAllMoney(startBlood, startRock, startMagic);
    }
}