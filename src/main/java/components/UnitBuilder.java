package components;

import jade.GameObject;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Unit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static jade.Window.getScene;

public class UnitBuilder extends Component{
    public ArrayList<UNMADE> queue= new ArrayList<>();
    public int alliedB =1;
    public transient Transform tr;
    @Override
    public UnitBuilder Clone(){
        return new UnitBuilder();
    }
    @Override
    public void begin(){
//        super.isactive=true;
//        if(this.gameObject.getComponent(CastAbilities.class)==null){
//            this.gameObject.addComponent(new CastAbilities());
//        };
//        CastAbilities ability=this.gameObject.getComponent(CastAbilities.class);
//        builder=ability.getAbility("Build unit");
//        ability.addAbility(builder);
        if(this.alliedB==0){
            this.alliedB=super.gameObject.allied;
        }

    }
    public void start() {
        this.tr = gameObject.getComponent(Transform.class);
    }
    @Override
    public void update(float dt){

        if(!queue.isEmpty()){
            UNMADE unm=queue.get(0);
            unm.time-=dt;
            if(unm.time<0){
                makeUnit(unm);
                queue.remove(unm);
            }
        }


    }

    public void makeUnit(UNMADE unm) {
        Vector2f position = new Vector2f(tr.position.x,tr.position.y-0.001f);
        GameObject unit = Unit.make(unm.name,position, alliedB);
        getScene().addGameObjectToScene(unit);
    }
    public void addqueue(String UnitName, Vector3f cost){
        if(getScene().addmoney(-cost.x, -cost.y, -cost.z,alliedB)) {
            UNMADE unm = new UNMADE(UnitName);
            queue.add(unm);
            queue.sort((s1, s2) -> (int) (s1.time - s2.time));
        }
    }
    @Override
    public void copyProperties(Component master) {
        for (UNMADE c :
                queue) {
            UNMADE clone = c.Clone();

            Field[] fields = c.getClass().getDeclaredFields();
            try {
                for (Field field : fields) {

                    boolean isTransient = Modifier.isTransient(field.getModifiers());
                    boolean isFinal = Modifier.isFinal(field.getModifiers());
                    if (isTransient || isFinal) {
                        continue;
                    }

                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                    if (isPrivate) {
                        field.setAccessible(true);
                    }
                    Object value = field.get(c);
                    field.set(clone, value);
                    if (isPrivate) {
                        field.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException ex) {
                System.out.println(c.getClass());

                throw new RuntimeException(ex);
            }

            ((UnitBuilder) master).queue.add(clone);
        }

    }





}
