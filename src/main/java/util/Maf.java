package util;

import org.jbox2d.common.Vec2;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class Maf {
    public static double distance(float position1, float position2, float position3, float position4){
        double pos1=position1,pos2=position2,pos3=position3,pos4=position4;
        return Math.sqrt( Math.pow( pos1-pos2,2)+Math.pow( pos3-pos4,2));

    }
    public static double distance(double pos1, double pos2, double pos3, double pos4){
        return Math.sqrt( Math.pow( pos1-pos2,2)+Math.pow( pos3-pos4,2));

    }
    public static double distance(int pos1, int pos2, int pos3, int pos4){
        return Math.sqrt( Math.pow( pos1-pos2,2)+Math.pow( pos3-pos4,2));

    }
    public static double distance(long position1, long position2, long position3, long position4){
        double pos1=position1,pos2=position2,pos3=position3,pos4=position4;
        return Math.sqrt( Math.pow( pos1-pos2,2)+Math.pow( pos3-pos4,2));

    }
    public static double distance(Vector2f pos1,Vector2f pos2){
        return distance(pos1.x,pos2.x,pos1.y,pos2.y);
    }
    public static double distance(Vector2d pos1, Vector2d pos2){
        return distance(pos1.x,pos2.x,pos1.y,pos2.y);
    }
    public static double distance(Vec2 pos1, Vec2 pos2){
        return distance(pos1.x,pos2.x,pos1.y,pos2.y);
    }

    public static double distance(Vec2 pos1, Vector2f pos2) {
        return distance(pos1.x,pos2.x,pos1.y,pos2.y);
    }
}
