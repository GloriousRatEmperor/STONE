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
    public static void setStartNow(){ //states that it is currently time 0 and resets frames
        time=System.currentTimeMillis();
        FrameTime=0;
    }
    public static void setStart(long tim){ // starts counting time from tim point and resets frames
        time=(tim);
        FrameTime=0;
    }
    public static long getStart(){
        return time;
    }
    public static void setFrame(float tim){
        FrameTime=(long) tim;
    }

}
