package components;

import imgui.ImGui;
import imgui.type.ImString;
import jade.GameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component {
    @Override
    public StateMachine Clone(){
        return new StateMachine();
    }
    private class StateTrigger {
        public String state;
        public String trigger;

        public StateTrigger() {}

        public StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger t2 = (StateTrigger)o;
            return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, trigger);
        }
    }

    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList<>();
    private transient AnimationState currentState = null;
    private String defaultStateTitle = "";

    public void refreshTextures() {
        for (AnimationState state : states) {
            state.refreshTextures();
        }
    }

    public void setDefaultState(String animationTitle) {
        for (AnimationState state : states) {

            if (state.title.equals(animationTitle)) {

                defaultStateTitle = animationTitle;
                if (currentState == null) {

                    currentState = state;
                }
                return;
            }
        }

        System.out.println("Unable to find default state '" + animationTitle + "'");
    }

    public void addState(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addState(AnimationState state) {
        this.states.add(state);
    }

    public void trigger(String trigger) {
        for (StateTrigger state : stateTransfers.keySet()) {
            if (state.state.equals(currentState.title) && state.trigger.equals(trigger)) {
                if (stateTransfers.get(state) != null) {
                    int newStateIndex = stateIndexOf(stateTransfers.get(state));
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }
    }

    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : states) {
            if (state.title.equals(stateTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    @Override
    public void start() {
        for (AnimationState state : states) {
            if (state.title.equals(defaultStateTitle)) {
                currentState = state;
                break;
            }
        }
    }

    @Override
    public void updateDraw() {
        if (currentState != null) {
            boolean animationFinished=currentState.updateDraw(gameObject.transform);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
                sprite.setTexture(currentState.getCurrentSprite().getTexture());
            }
            if(animationFinished){
                currentState.reset();
                currentState=null;
            }
        }
    }

    @Override
    public void LevelEditorStuffImgui() {
        for (AnimationState state : states) {
            ImString title = new ImString(state.title);
            ImGui.inputText("State: ", title);
            state.title = title.get();

            int index = 0;
            for (Frame frame : state.animationFrames) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + index + ") Time: ", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }
    @Override
    public List<GameObject> EditorGui(List<GameObject> activegameObjects,HashMap<String,String> guiData) {
        for (AnimationState state : states) {
            ImString title = new ImString(state.title);
            ImGui.inputText("State: ", title);
            state.title = title.get();

            int index = 0;
            for (Frame frame : state.animationFrames) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + index + ") Time: ", tmp, 0.01f);
                if (frame.frameTime != tmp[0]) {
                    frame.frameTime = tmp[0];
                    for (GameObject go : activegameObjects) {
                        StateMachine comp = go.getComponent(StateMachine.class);
                        if (comp != null) {
                            for (AnimationState cstate : comp.states) {
                                if(title.equals(new ImString(cstate.title))) {
                                    Frame cfram = cstate.animationFrames.get(index);
                                    cfram.frameTime += tmp[0] - frame.frameTime;
                                }
                            }
                        }
                    }
                }
                index++;
            }
        }
        return activegameObjects;
    }
}
