package util;

import static jade.Window.getScene;

public class Variables {
    public static int startBlood = 2000000;
    public static int startRock = 2000000;
    public static int startMagic = 200000;


    public static void start() {

        getScene().setAllMoney(startBlood, startRock, startMagic);
    }
}