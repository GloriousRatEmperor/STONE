package util;

public class UniTime {
    private static ThreadLocal<Long> time= new ThreadLocal<>(){
        @Override
        protected Long initialValue()
        {
            return 0L;
        }
    };

    private static ThreadLocal<Long> FrameTime= new ThreadLocal<>(){
        @Override
        protected Long initialValue()
        {
            return 0L;
        }
    };
    public static float getExact(){
        // the time in seconds from when the game began to exactly right now regardless of physics or lag
        int timeInMilis= (int) (System.currentTimeMillis()-time.get());
        return timeInMilis/1000f;
    }

    public static float getFrame(){
        // the time in seconds from when the game began to the last physics frame
        return FrameTime.get();
    }
    public static void setStartNow(){ //states that it is currently time 0 and resets frames
        time.set(System.currentTimeMillis());
        FrameTime.set(0L);
    }
    public static void setStart(long tim){ // starts counting time from tim point and resets frames
        time.set(tim);
        FrameTime.set(0L);
    }
    public static long getStart(){
        return time.get();
    }
    public static void setFrame(float tim){
        FrameTime.set((long)tim);
    }

}
