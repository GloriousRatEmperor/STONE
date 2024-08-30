package JavaUnrelated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Averager {


    public static void main(String[] args) throws Exception {
        double average = 0;
        int maxRoll = 4;
        int dice = 4;
        int randomNum;
        double totalAvg=0;
        for (int a = 0; a < 10000000; a++) {

            List<Double> numbers = new ArrayList<>();
            for (int i = 0; i < dice; i++) {
                randomNum = ThreadLocalRandom.current().nextInt(1, maxRoll + 1);
                numbers.add((double) randomNum);
            }
            numbers.sort(Comparator.comparingDouble(s -> s));
            if(numbers.get(0)<(maxRoll / 2)+0.5) {
                numbers.set(0, (maxRoll / 2) + 0.5);
            }
            for (double P:numbers) {

                average+=P;

            }
            average/=dice;
            totalAvg+=average;
            average=0;
        }
        totalAvg/=10000000;
        System.out.println(totalAvg*dice);
    }
}