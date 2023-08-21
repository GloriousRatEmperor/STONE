package jade;

public class Time {
    private long beginTime;
    public Time() {
        this.beginTime= System.currentTimeMillis();
    }

    public float getTime(){
        int timeInMilis= (int) (System.currentTimeMillis()-beginTime);
        return timeInMilis/1000f;
    }
    public void setBeginTime(long currentTimeSeconds){
        this.beginTime=  currentTimeSeconds;
    }
}
