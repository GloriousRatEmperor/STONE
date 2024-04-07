package util;

public class UniTime {
    private static long time;
    private static long FrameTime;
    public static float getExact(){
        int timeInMilis= (int) (System.currentTimeMillis()-time);
        return timeInMilis/1000f;
    }

    public static float getFrame(){
        int timeInMilis= (int) (FrameTime-time);
        return timeInMilis/1000f;
    }
    public static void set(float tim){
        time=(long)tim;
        FrameTime=0;
    }
    public static void setFrame(float tim){
        FrameTime=(long) tim;
    }

}
