package jade;

import java.util.concurrent.ThreadLocalRandom;

public class Rng {
     public static int randint(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
     public static int wild(int min, int max,float power,float divider){
         double number=ThreadLocalRandom.current().nextInt(min, max + 1);
         boolean nega=false;
         if(number<0){
             number*=-1;
             nega=true;
         }
         number=Math.pow(number, power)/Math.pow(divider, power);
         if(nega){
             return (int) -number;
         }
        return (int) number;
    }
}
