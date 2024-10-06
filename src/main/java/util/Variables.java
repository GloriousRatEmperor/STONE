package util;

import static jade.Window.getScene;

public class Variables {
    public static int startBlood = 5000;
    public static int startRock = 5000;
    public static int startMagic = 50;


    public static void start() {

        getScene().setAllMoney(startBlood, startRock, startMagic);
    }
}