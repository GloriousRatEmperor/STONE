package SubComponents;

import jade.GameObject;

import java.util.List;

public interface SubComponent {
    public void destroy();
    public String Imgui(int AbilitySize, List<GameObject> activeGameObjects, int ID);
    public void EditorImgui();
}
