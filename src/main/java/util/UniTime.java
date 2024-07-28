package util;

public class UniTime {
    private static long time;
    private static long FrameTime;
    public static float getExact(){
        // the time in seconds from when the game began to exactly right now regardless of physics or lag
        int timeInMilis= (int) (System.currentTimeMillis()-time);
        return timeInMilis/1000f;
    }

    public static float getFrame(){
        // the time in seconds from when the game began to the last physics frame
        return FrameTime;
    }
    public static void set(long tim){
        time=(tim);
        FrameTime=0;
    }
    public static void setFrame(float tim){
        FrameTime=(long) tim;
    }

}
