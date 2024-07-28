package editor;

import imgui.ImGui;
import imgui.type.ImInt;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MenuBar {
    private String[] guiLevelsaves;
    public MenuBar(){
        updateLevels();

    }
    private void updateLevels(){
        ArrayList<String> guiLevels= new ArrayList();
        File directoryPath = new File("ZlevelSaves");
        String[] names= directoryPath.list();
        guiLevels.add("Save");
        guiLevels.addAll(Arrays.asList(names));

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


    public void imgui() {
        ImGui.beginMenuBar();
        if (ImGui.beginMenu("File")) {

            if (ImGui.menuItem("LoadDICKS WTH IS THIS", "Ctrl+O")) {
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }

            ImInt inde=new ImInt(0);
            ImGui.setNextItemWidth(100);
            if (ImGui.combo("",inde, guiLevelsaves, guiLevelsaves.length)) {
                if(inde.get()!=0) {
                    Event event = new Event(EventType.SaveLevel);
                    event.strargs = guiLevelsaves[inde.get()];
                    EventSystem.notify(null, event);
                    updateLevels();
                }
            }

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
