package renderer;

import jade.Camera;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;
import util.JMath;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 100000;

    private static List<Line2D> lines = new ArrayList<>();
    // 6 floats per vertex, 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;
    private static Vector3f green=new Vector3f(0,1,0);

    private static boolean started = false;

    public static void start() {
        // Generate the vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create the vbo and buffer some memory
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Enable the vertex array attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(2.0f);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        // Remove dead lines
        for (int i=0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }


    public static void draw() {
        if (lines.size() == 0) return;

        int index = 0;
        for (Line2D line : lines) {
            for (int i=0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                // Load position

                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = 10.0f;

                // Load the color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);

        // Use our shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        // Bind the vao
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        // Draw the batch
        glDrawArrays(GL_LINES, 0, lines.size());

        // Disable Location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // Unbind shader
        shader.detach();
    }

    // ==================================================
    // Add line2D methods
    // ==================================================
    public static void addLine2D(Vector2f from, Vector2f to) {

        addLine2D(from, to, green, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        Camera camera = Window.getScene().camera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.position).
                add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom())).
                add(new Vector2f(4.0f, 4.0f));
        boolean lineInView =
                ((from.x >= cameraLeft.x && from.x <= cameraRight.x) && (from.y >= cameraLeft.y && from.y <= cameraRight.y)) ||
                ((to.x >= cameraLeft.x && to.x <= cameraRight.x) && (to.y >= cameraLeft.y && to.y <= cameraRight.y));
        if (lines.size() >= MAX_LINES || !lineInView) {
            return;
        }
        DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
    }
    public static boolean addLine2DReturnSuccess(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        Camera camera = Window.getScene().camera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.position).
                add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom())).
                add(new Vector2f(4.0f, 4.0f));
        boolean lineInView =
                ((from.x >= cameraLeft.x && from.x <= cameraRight.x) && (from.y >= cameraLeft.y && from.y <= cameraRight.y)) ||
                        ((to.x >= cameraLeft.x && to.x <= cameraRight.x) && (to.y >= cameraLeft.y && to.y <= cameraRight.y));
        if (lines.size() >= MAX_LINES || !lineInView) {
            return false;
        }
        DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
        return true;
    }
    public static void addStrip2D(Vector2f from, final Vector2f direction,float stripeLen,boolean ends, Vector3f color, int lifetime){//makes a stripped line with segments of length [stripeLen]
        //from [from] in a direction to point [direction] towards which it is aimed at, if [ends] is false it does not end at direction. [color] and [lifetime] are that of all line segments

        Vector2f dir= new Vector2f( direction.x-from.x,direction.y-from.y);

        float length=((float)Math.sqrt(Math.pow(dir.x,2)+Math.pow(dir.y,2)));
        dir.mul(stripeLen/length);
        int maxcount;
        if(ends){
            maxcount=(int) (length/(stripeLen));
        }else{
            maxcount=Integer.MAX_VALUE;
        }

        Camera camera = Window.getScene().camera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.position).
                add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom())).
                add(new Vector2f(4.0f, 4.0f));


        int minX,maxX;
        minX=(int) ((cameraLeft.x-from.x)/dir.x);
        maxX=(int) ((cameraRight.x-from.x)/dir.x);

        if(minX>maxX){
            int tmp=minX;
            minX=maxX;
            maxX=tmp;
        }

        int minY,maxY;
        minY=(int) ((cameraLeft.y-from.y)/dir.y);
        maxY=(int) ((cameraRight.y-from.y)/dir.y);

        if(minY>maxY){
            int tmp=minY;
            minY=maxY;
            maxY=tmp;
        }

        int minimum=0;
        if(minX>minY){
            minimum=minX;
        }else{
            minimum=minY;
        }
        if(minimum<0){
            minimum=0;
        }
        int maximum=0;
        if(maxX>maxY){
            maximum=maxY;
        }else{
            maximum=maxX;
        }

        if(maximum>maxcount){
            maximum=maxcount;
        }
        if(maximum<minimum){
            return;
        }


        int currentStep=minimum%2;
        from.x += dir.x * 2*currentStep;
        from.y += dir.y * 2*currentStep;
        while (currentStep<maximum-1) {
            if (lines.size() >= MAX_LINES) {
                return;
            }
            DebugDraw.lines.add(new Line2D(new Vector2f(from), new Vector2f(from.x + dir.x, from.y + dir.y), color, lifetime));
            currentStep+=2;
            from.x += dir.x * 2;
            from.y += dir.y * 2;
        }

        if(!from.equals(direction,0.0001f)){
            DebugDraw.addLine2DReturnSuccess(from,direction,color,lifetime);
        }



    }
    public static void addStrip2DOfLen(Vector2f from, Vector2f direction,float stripeLen,boolean ends, Vector3f color, int lifetime,float len){//makes a stripped line with segments of length [stripeLen]
        //from [from] in a direction to point [direction] towards which it is aimed at, if [ends] is false it does not end at direction. [color] and [lifetime] are that of all line segments

        Vector2f dir= new Vector2f( direction.x-from.x,direction.y-from.y);

        float length=((float)Math.sqrt(Math.pow(dir.x,2)+Math.pow(dir.y,2)));
        dir.mul(len/length);
        direction=new Vector2f(from.x+dir.x,from.y+dir.y);
        length=len;
        dir.mul(stripeLen/length);

        int maxcount;
        if(ends){
            maxcount=(int) (length/(stripeLen));
        }else{
            maxcount=Integer.MAX_VALUE;
        }

        Camera camera = Window.getScene().camera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.position).
                add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom())).
                add(new Vector2f(4.0f, 4.0f));


        int minX,maxX;
        minX=(int) ((cameraLeft.x-from.x)/dir.x);
        maxX=(int) ((cameraRight.x-from.x)/dir.x);

        if(minX>maxX){
            int tmp=minX;
            minX=maxX;
            maxX=tmp;
        }

        int minY,maxY;
        minY=(int) ((cameraLeft.y-from.y)/dir.y);
        maxY=(int) ((cameraRight.y-from.y)/dir.y);

        if(minY>maxY){
            int tmp=minY;
            minY=maxY;
            maxY=tmp;
        }

        int minimum=0;
        if(minX>minY){
            minimum=minX;
        }else{
            minimum=minY;
        }
        if(minimum<0){
            minimum=0;
        }
        int maximum=0;
        if(maxX>maxY){
            maximum=maxY;
        }else{
            maximum=maxX;
        }

        if(maximum>maxcount){
            maximum=maxcount;
        }
        if(maximum<minimum){
            return;
        }


        int currentStep=minimum%2;
        from.x += dir.x * 2*currentStep;
        from.y += dir.y * 2*currentStep;
        while (currentStep<maximum-1) {
            if (lines.size() >= MAX_LINES) {
                return;
            }
            DebugDraw.lines.add(new Line2D(new Vector2f(from), new Vector2f(from.x + dir.x, from.y + dir.y), color, lifetime));
            currentStep+=2;
            from.x += dir.x * 2;
            from.y += dir.y * 2;
        }

        if(!from.equals(direction,0.0001f)){
            DebugDraw.addLine2DReturnSuccess(from,direction,color,lifetime);
        }



    }

    // ==================================================
    // Add Box2D methods
    // ==================================================
    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {

        addBox2D(center, dimensions, rotation, green, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color) {
        addBox2D(center, dimensions, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation,
                                Vector3f color, int lifetime) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

        Vector2f[] vertices = {
              new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
              new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (rotation != 0.0f) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[1], color, lifetime);
        addLine2D(vertices[0], vertices[3], color, lifetime);
        addLine2D(vertices[1], vertices[2], color, lifetime);
        addLine2D(vertices[2], vertices[3], color, lifetime);
    }

    // ==================================================
    // Add Circle methods
    // ==================================================
    public static void addCircle(Vector2f center, float radius) {

        addCircle(center, radius, green, 1);
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color) {
        addCircle(center, radius, color, 1);
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color, int lifetime) {
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i=0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            JMath.rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(center);

            if (i > 0) {
                addLine2D(points[i - 1], points[i], color, lifetime);
            }
            currentAngle += increment;
        }

        addLine2D(points[points.length - 1], points[0], color, lifetime);
    }
}
