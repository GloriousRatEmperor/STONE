package util;

import jade.Rng;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapMaker {
    public int space=2048;
    public int count=1;
    public int colorMax=245;
    //allows for more precision due to there being intiger constraints
    public int colorEmbiggen=4;
    public int colorMin=10;
    public float dechaos=1.3f;
    public int randchange=250*4;
    public int boxCount=8;
    public int maxrand=200;

    public int colorLotto=100;
    public int pow=3;
    public int division=20;
    public int size=count*space;
    private Vector3i last= new Vector3i(Rng.randint(colorMin * colorEmbiggen, colorMax * colorEmbiggen),
            Rng.randint(colorMin * colorEmbiggen, colorMax * colorEmbiggen),
            Rng.randint(colorMin * colorEmbiggen, colorMax * colorEmbiggen));
    public int colorRand=Rng.randint(-maxrand,maxrand);

    public int boxsize = size / 2;
    public HashMap<Vector2i, Vector3i> generate(){
        HashMap<Vector2i, Vector3i> map = new HashMap<>();
        colorRand = 100;
        last.x = Rng.randint(colorMin, colorMax);
        last.y = Rng.randint(colorMin, colorMax);
        last.z = Rng.randint(colorMin, colorMax);
        map.put(new Vector2i(-boxsize, -boxsize), new Vector3i(last.x, last.y, last.z));
        last.x = Rng.randint(colorMin, colorMax);
        last.y = Rng.randint(colorMin, colorMax);
        last.z = Rng.randint(colorMin, colorMax);
        map.put(new Vector2i(boxsize, -boxsize), new Vector3i(last.x, last.y, last.z));
        last.x = Rng.randint(colorMin, colorMax);
        last.y = Rng.randint(colorMin, colorMax);
        last.z = Rng.randint(colorMin, colorMax);
        map.put(new Vector2i(boxsize, boxsize), new Vector3i(last.x, last.y, last.z));
        last.x = Rng.randint(colorMin, colorMax);
        last.y = Rng.randint(colorMin, colorMax);
        last.z = Rng.randint(colorMin, colorMax);
        map.put(new Vector2i(-boxsize, boxsize), new Vector3i(last.x, last.y, last.z));
        List<Vector2i> squarePositions = new ArrayList<>();
        for (int i = 0; i < boxCount; i++) {

            for (int a = -size / 2 + boxsize; a < size / 2f; a += boxsize * 2) {
                for (int c = -size / 2 + boxsize; c < size / 2; c += boxsize * 2) {
                    squarePositions.add(new Vector2i(a, c));
                }
            }


            for (Vector2i pos : squarePositions) {
                last.x = (map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).x + map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).x +
                        map.get(new Vector2i(pos.x - boxsize, pos.y + boxsize)).x + map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).x) / 4;
                last.y = (map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).y + map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).y +
                        map.get(new Vector2i(pos.x - boxsize, pos.y + boxsize)).y + map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).y) / 4;
                last.z = (map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).z + map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).z +
                        map.get(new Vector2i(pos.x - boxsize, pos.y + boxsize)).z + map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).z) / 4;
                map.put(new Vector2i(pos.x, pos.y), randomC());
                last.x = (map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).x + map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).x +
                        map.get(new Vector2i(pos.x, pos.y)).x) / 3;
                last.y = (map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).y + map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).y +
                        map.get(new Vector2i(pos.x, pos.y)).y) / 3;
                last.z = (map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).z + map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).z +
                        map.get(new Vector2i(pos.x, pos.y)).z) / 3;
                map.put(new Vector2i(pos.x, pos.y - boxsize), randomC());
                last.x += (-map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).x + map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).x) / 3;
                last.y += (-map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).y + map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).y) / 3;
                last.z += (-map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).z + map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).z) / 3;
                map.put(new Vector2i(pos.x + boxsize, pos.y), randomC());
                last.x += (-map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).x + map.get(new Vector2i(pos.x - boxsize, pos.y + boxsize)).x) / 3;
                last.y += (-map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).y + map.get(new Vector2i(pos.x - boxsize, pos.y + boxsize)).y) / 3;
                last.z += (-map.get(new Vector2i(pos.x + boxsize, pos.y - boxsize)).z + map.get(new Vector2i(pos.x - boxsize, pos.y + boxsize)).z) / 3;
                map.put(new Vector2i(pos.x, pos.y + boxsize), randomC());
                last.x += (-map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).x + map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).x) / 3;
                last.y += (-map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).y + map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).y) / 3;
                last.z += (-map.get(new Vector2i(pos.x + boxsize, pos.y + boxsize)).z + map.get(new Vector2i(pos.x - boxsize, pos.y - boxsize)).z) / 3;
                map.put(new Vector2i(pos.x - boxsize, pos.y), randomC());


            }
            boxsize /= 2;
            randchange /= dechaos;
            squarePositions.clear();
        }
        return map;
    }
    private Vector3i randomC() {
        //ugh writing the random was annoying
        int r=Math.max(Math.min( (last.x*colorEmbiggen+Rng.randint(-randchange,+randchange)+Rng.wild(-colorLotto,colorLotto,pow,division))/colorEmbiggen,colorMax),colorMin);
        int g=Math.max(Math.min( (last.y*colorEmbiggen+Rng.randint(-randchange,+randchange)+Rng.wild(-colorLotto,colorLotto,pow,division))/colorEmbiggen,colorMax),colorMin);
        int b=Math.max(Math.min( (last.z*colorEmbiggen+Rng.randint(-randchange,+randchange)+Rng.wild(-colorLotto,colorLotto,pow,division))/colorEmbiggen,colorMax),colorMin);


        return new Vector3i(r,g,b);
    }
}
