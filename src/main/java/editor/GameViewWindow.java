package editor;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import jade.MouseListener;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GameViewWindow {

    private boolean isPlaying = false;
    private boolean windowIsHovered;
    private String[] guiLevelsaves;
    private String[] guiLevelLoads;
    private String lastLevel="permalevel.txt";
    public GameViewWindow(){
        updateLevels();

    }
    private void updateLevels(){
        ArrayList<String> guiLevels= new ArrayList();
        File directoryPath = new File("ZlevelSaves");
        String[] names= directoryPath.list();
        guiLevels.add("Load");
        guiLevels.addAll(Arrays.asList(names));


        guiLevelLoads=new String[guiLevels.size()];
        guiLevelLoads=guiLevels.toArray(guiLevelLoads);

        guiLevels.set(0,"Save");

        String levelname="level1.txt";
        int levelnumber=1;
        while (Arrays.stream(names).anyMatch(levelname::equals)){
            levelnumber+=1;
            levelname="level"+levelnumber+".txt";
        }
        guiLevels.add(levelname);

        guiLevelsaves=new String[guiLevels.size()];
        guiLevelsaves=guiLevels.toArray(guiLevelsaves);


    }

    public void imgui(boolean playing) {

        imgui.ImGuiIO io = ImGui.getIO();
        ImGui.setNextWindowSize(io.getDisplaySizeX(),io.getDisplaySizeY());
        ImGui.setNextWindowPos(0,0);
        ImGuiStyle style = ImGui.getStyle();
        style.setTabBorderSize(0);
        style.setFrameBorderSize(0);
        style.setFramePadding(0,0);
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                        | ImGuiWindowFlags.MenuBar| ImGuiWindowFlags.NoResize| ImGuiWindowFlags.NoBringToFrontOnFocus
                        | ImGuiWindowFlags.NoTitleBar| ImGuiWindowFlags.NoCollapse| ImGuiWindowFlags.NoDecoration|ImGuiWindowFlags.NoBackground);


        ImGui.beginMenuBar();

        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameRequestPlay));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
        if (ImGui.menuItem("ScanForDupes", "", isPlaying, !isPlaying)) {
            EventSystem.notify(null, new Event(EventType.Scan));
        }
        if (ImGui.menuItem("Load", "Ctrl+O")) {
            Event event = new Event(EventType.LoadLevel);
            event.strargs = lastLevel;
            EventSystem.notify(null,event);
        }

        //if(!playing) {
            if (ImGui.menuItem("Save", "Ctrl+S")) {

                Event event = new Event(EventType.SaveLevel);
                event.strargs = lastLevel;
                EventSystem.notify(null, event);
            }
        //}
        ImInt inde=new ImInt(0);
        ImGui.setNextItemWidth(120);
        if (ImGui.combo("",inde, guiLevelLoads, guiLevelLoads.length)) {
            if(inde.get()!=0) {
                Event event = new Event(EventType.ChangeLevel);
                event.strargs = guiLevelLoads[inde.get()];
                lastLevel=guiLevelLoads[inde.get()];
                EventSystem.notify(null, event);
                updateLevels();
            }
        }
        if(!playing) {
            inde=new ImInt(0);
            ImGui.setNextItemWidth(120);
            if (ImGui.combo(".",inde, guiLevelsaves, guiLevelsaves.length)) {
                if(inde.get()!=0) {
                    Event event = new Event(EventType.SaveLevel);
                    event.strargs = guiLevelsaves[inde.get()];
                    lastLevel=guiLevelsaves[inde.get()];
                    EventSystem.notify(null, event);
                    updateLevels();
                }
            }

        }
        ImGui.endMenuBar();


        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewport();

        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.imageButton(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);
        windowIsHovered = ImGui.isItemHovered();

        MouseListener.setGameViewportPos(new Vector2f(windowPos.x, windowPos.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    public boolean getWantCaptureMouse() {
        return windowIsHovered;
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }
}
