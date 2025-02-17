package util;

import static jade.Window.getScene;

public class Variables {
    public static int startBlood = 500;
    public static int startRock = 500;
    public static int startMagic = 500;


    public static void start() {

        getScene().setAllMoney(startBlood, startRock, startMagic);
    }
}