package components;

import SubComponents.Effects.Effect;
import SubComponents.Effects.ExplodeOnDeath;
import SubComponents.Effects.ExplodingProjectiles;
import SubComponents.Effects.SpeedUp;
import SubComponents.SubComponent;
import enums.EffectName;
import imgui.ImGui;
import imgui.type.ImInt;
import jade.GameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Effects extends Component {
    private transient List<Effect> effects = new ArrayList<>();
    public Effects(){
        subComponents=effects;
    }

    @Override
    public Effects Clone(){
        return new Effects();
    }
    @Override
    public void update(float dt){
        if (effects.size()>0) {
            for (Effect e : effects) {
                e.update(dt);
            }
            Effect effect =effects.get(0);
            if (effect.durationNow<=0){
                effect.expire(super.gameObject);
                effects.remove(effect);
            }
        }
    }
    public void addEffect(Effect effect){
        if(isactive) {
            effect.apply(super.gameObject);
        }
        effect.owner=this;
        effects.add(effect);
        effects.sort((s1, s2) -> (int) (s1.durationNow-s2.durationNow));
    }
    @Override
    public void begin(){
        isactive=true;
        for(Effect e:effects){
            e.apply(super.gameObject);
        }
    }
    public Effect getEffect(EffectName name, float duration, float mult){
        Effect effect = switch (name) {
                case speedUp ->


                    new SpeedUp(duration,mult);
            case explodeOnDeath ->
                new ExplodeOnDeath(duration,mult);
            case explodingProjectiles ->
                new ExplodingProjectiles(duration,mult);

                default -> null;
        };
        if(effect!=null){
            effect.id=name.getId();
            effect.name=name.toString();
        }
        return effect;
    }
    public ArrayList<Effect> getEffects(String type){
        ArrayList<Effect> eff=new ArrayList<>();
        for (Effect e:effects){
            if(e.type.equals(type)){
                eff.add(e);
            }
        }
        return eff;
    }
    public Effect getEffect(EffectName name){
       return getEffect(name,5,1);
    }
    @Override
    public void addSubComponent(SubComponent c) {
        Effect e=((Effect)c);
        addEffect(e);
    }
    public boolean hasEffect(Class<Effect> clas) {
        for (Effect e : effects) {
            if (Objects.equals(e.getClass(),clas)) {
                return true;
            }
        }
        return false;
    }
    public List<GameObject> EditorGui(List<GameObject> activegameObjects, HashMap<String, String> guiData) {
        super.EditorGui(activegameObjects, guiData);
        String[] enumValues = getEnumValues(EffectName.class);
        ImInt index = new ImInt(0);


        if (ImGui.combo("Add Effect", index, enumValues, enumValues.length)) {
            if (index.get() != 0) {
                for (GameObject go : activegameObjects) {
                    Effects cast = go.getComponent(Effects.class);
                    if (cast != null) {
                        EffectName name = EffectName.class.getEnumConstants()[index.get()];
                        if (!cast.hasEffect(name.getId())) {
                            cast.addEffect(cast.getEffect(name));
                            guiData.put("guiRemoveEffect", guiData.get("guiRemoveEffect") + '0' + name);
                        }
                    }

                }
            }
        }
        ImInt inde = new ImInt(0);
        String[] s = guiData.get("guiRemoveEffect").split("0");
        if (ImGui.combo("Remove Effect", inde, s, s.length)) {
            String name = s[inde.get()];
            if (inde.get() != 0) {
                for (GameObject go : activegameObjects) {
                    Effects cast = go.getComponent(Effects.class);
                    if (cast != null) {
                        cast.removeEffect(name);
                    }
                    guiData.put("guiRemoveEffect", guiData.get("guiRemoveEffect").replace('0' + name, ""));

                }
            }
            this.removeEffect(name);
        }
        return activegameObjects;
    }
    @Override
    public void updateData(HashMap<String, String> guiData, boolean running) {
        if (!running) {
            StringBuilder alreadyHas = new StringBuilder(guiData.getOrDefault("guiRemoveEffect", ""));

            if (Objects.equals(alreadyHas.toString(), "")) {
                alreadyHas.append("Remove Effect");
            }
            List<String> names = new ArrayList<>(List.of(alreadyHas.toString().split(",")));
            for (Effect a : effects) {
                if (!names.contains(a.name)) {
                    alreadyHas.append('0').append(a.name);
                }
            }
            guiData.put("guiRemoveEffect", alreadyHas.toString());

        }
        for (Effect e:effects){
            e.updateDesc();
        }
    }
    public void removeEffect(int id) {
        for (Effect a : effects) {
            if (a.id==id) {
                effects.remove(a);
                break;
            }
        }

    }
    public void removeEffect(String name) {
        for (Effect a : effects) {
            if (Objects.equals(a.name, name)) {
                effects.remove(a);
                break;
            }
        }

    }
    public boolean hasEffect(int id) {
        for (Effect a : effects) {
            if (a.id== id) {
                return true;
            }
        }
    return false;

    }
}
