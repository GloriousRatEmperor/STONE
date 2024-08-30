package components;

import SubComponents.Effects.Effect;

import java.util.ArrayList;
import java.util.List;

public class Effects extends Component {
    public static final int ImguiGroup=2;
    private List<Effect> effects = new ArrayList<>();
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
        effect.apply(super.gameObject);
        effects.add(effect);
        effects.sort((s1, s2) -> (int) (s1.durationNow-s2.durationNow));
    }
}
