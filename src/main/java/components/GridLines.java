package components;

import jade.Camera;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

public class GridLines extends Component {

    @Override
    public void editorUpdateDraw() {
        Camera camera = Window.getScene().camera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();



        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        float width = (int)(projectionSize.x * camera.getZoom()) + (5 * Settings.GRID_WIDTH);
        float height = (int)(projectionSize.y * camera.getZoom()) + (5 * Settings.GRID_HEIGHT);
        int spacing=1;
        int maxLines = Math.max(numVtLines, numHzLines);
        while(maxLines>200){
            maxLines/=5;
            spacing*=5;
        }
        float firstX = ((int)Math.floor(cameraPos.x / (Settings.GRID_WIDTH*spacing))) * Settings.GRID_HEIGHT*spacing;
        float firstY = ((int)Math.floor(cameraPos.y / (Settings.GRID_HEIGHT*spacing))) * Settings.GRID_HEIGHT*spacing;
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        for (int i=0; i < maxLines; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i*spacing);
            float y = firstY + (Settings.GRID_HEIGHT * i*spacing);

            if (i < numVtLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }

}
