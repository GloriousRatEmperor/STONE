package components.SubComponents.Abilities;

import enums.AbilityName;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.UnitUtils.ProjectileCreator;

import static jade.Window.getScene;

public abstract class ShootProjectile extends Ability{
    public String projectileName;

    public ShootProjectile(AbilityName type) {
        super(type);
        targetable=true;

    }
    @Override
    public boolean cast(final Vector2f pos,GameObject self, GameObject target){
        GameObject projectile;
        if(requiresTarget){
            projectile= ProjectileCreator.makeProjectile(projectileName, self.getUid(), self.transform.position, target.transform,self.allied);
        }else{
            projectile= ProjectileCreator.makeProjectile(projectileName, self.getUid(), self.transform.position, pos,self.allied);
        }
        getScene().addGameObjectToScene(projectile);
        return true;
    }
    @Override
    public void castGui(Vector2f castPos, Vector2f selfPos) {
        if(castPos.equals(selfPos,0.0001f)){
            return;
        }
        DebugDraw.addStrip2DOfLen(new Vector2f(selfPos),new Vector2f(castPos),1f,true,new Vector3f(0.5f,0,1),1,range);

    }

}
