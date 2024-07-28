package renderer;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import util.AssetPool;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static jade.Window.checkIfNewFloor;
import static jade.Window.getFloor;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_NEAREST;
import static org.lwjgl.opengl.GL15.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL15.glGetError;
import static org.lwjgl.opengl.GL15.glTexParameteri;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;
    private int FVbo;
    private int Vao;
    private int eboID;
    public int[] indices;
    private Texture tex;
    private float[] vertices;

    public Renderer() {

        this.batches = new ArrayList<>();
    }
    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.zIndex() == sprite.gameObject.transform.zIndex && batch.shaderId == sprite.shaderIndex) {
                Texture tex = sprite.getTexture();
                if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE,
                    sprite.gameObject.transform.zIndex, this,sprite.shaderIndex);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render(Boolean picking) {
        currentShader.use();
        for (int i = 0; i < batches.size(); i++) {
            RenderBatch batch = batches.get(i);
            batch.render(picking);
        }
    }

    public void renderFloor() {
        if(checkIfNewFloor()){
            HashMap<Vector2i,Vector3i> map=getFloor();
            setFloor(map);
        }

        currentShader.use();

        currentShader.uploadTexture("sampler",0);
        glActiveTexture(GL_TEXTURE0);
        tex.bind();
        currentShader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        currentShader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());


        glBindVertexArray(Vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glDrawElements(GL_TRIANGLES,indices.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        tex.unbind();
        currentShader.detach();
        if(!(glGetError() ==0)) {
            System.out.println(glGetError());
        }
    }

    public void setFloor(HashMap<Vector2i, Vector3i> map) {
        int count=map.get(new Vector2i(0,1)).x;
        int spacing=map.get(new Vector2i(0,1)).y;
        vertices= new float[count * count * 8];
        indices =generateIndices2(count);
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/background.png");
        Sprite sprite =items.getSprite(0);
        this.tex=sprite.getTexture();
        loadMap(map,spacing,count);


        Vao = glGenVertexArrays();
        glBindVertexArray(Vao);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        FVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, FVbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);





        IntBuffer elementBuffer = BufferUtils.createIntBuffer(indices.length);
        elementBuffer.put(indices).flip();


        eboID = glGenBuffers();


        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_DYNAMIC_DRAW);


        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4,  GL_FLOAT, false, 8 * Float.BYTES, 2 * Float.BYTES);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2,  GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);




//    Vao = glGenVertexArrays();
//    glBindVertexArray(Vao);
//    FloatBuffer
//
//            eboID = glGenBuffers();
//    float[] indices =generateIndices2();
//
//
//
//    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
//    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
//    FVbo = glGenBuffers();
//    glBindBuffer(GL_ARRAY_BUFFER, FVbo);
//    loadMap(map);
//    glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
//    glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
//    glEnableVertexAttribArray(0);
//    glVertexAttribPointer(0, 2, GL_FLOAT, false, 6 * Float.BYTES, 0);
//
//    glEnableVertexAttribArray(1);
//    glVertexAttribPointer(1, 4, GL_FLOAT, false, 6 * Float.BYTES, 2 * Float.BYTES);
    }

    private void loadMap(HashMap<Vector2i, Vector3i> map,int spacing,int count) {

        int offset = 0;
        int size=count*spacing;
        int c=1;
        int l=1;
        for (int i = 0; i < count; i++) {
            l*=-1;
            l+=1;
            for (int b = 0; b < count; b++) {
                c*=-1;
                c+=1;
                Vector2i pos = new Vector2i(-size/2 + spacing * i, -size/2 + spacing * b);
                Vector3i color = map.get(pos);

                // Load position
                vertices[offset] =pos.x/2f;
                vertices[offset + 1] = pos.y/2f;

                // Load color
                vertices[offset + 2] = color.x/255f;
                vertices[offset + 3] = color.y/255f;
                vertices[offset + 4] = color.z/255f;
                vertices[offset + 5] = 1;

                vertices[offset + 6] = l;
                vertices[offset + 7] = c;


                offset += 8;

            }
        }


    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[499*499*2*3];
        for (int i = 0; i < 499*499; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 0;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    private int[] generateIndices2(int count) {
       int[] indices = new int[(count-1) * (count-1) * 6];
        int offset = 0;
        for (int x = 0; x < count-1; x++) {
            for (int y = 0; y < count-1; y++) {
                indices[offset] = x + count * y;
                indices[offset + 1] = x + 1 + count * y;
                indices[offset + 2] = x + count * (y + 1);

                indices[offset + 3] = x + 1 + count * y;
                indices[offset + 4] = x + count * (y + 1);
                indices[offset + 5] = x + 1 + count * (y + 1);
                offset += 6;
            }
        }



        return indices;
    }
}