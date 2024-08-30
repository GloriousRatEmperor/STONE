package physics2d;

import components.Component;
import jade.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class JadeContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Component objAA = (Component) contact.getFixtureA().getUserData();
        Component objBB = (Component) contact.getFixtureB().getUserData();
        GameObject objA=objAA.gameObject ;
        GameObject objB=objBB.gameObject;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.beginCollision(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.beginCollision(objA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Component objAA = (Component) contact.getFixtureA().getUserData();
        Component objBB = (Component) contact.getFixtureB().getUserData();
        GameObject objA=objAA.gameObject ;
        GameObject objB=objBB.gameObject;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.endCollision(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.endCollision(objA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        Component objAA = (Component) contact.getFixtureA().getUserData();
        Component objBB = (Component) contact.getFixtureB().getUserData();
        GameObject objA=objAA.gameObject ;
        GameObject objB=objBB.gameObject;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.preSolve(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.preSolve(objA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        Component objAA = (Component) contact.getFixtureA().getUserData();
        Component objBB = (Component) contact.getFixtureB().getUserData();
        GameObject objA=objAA.gameObject ;
        GameObject objB=objBB.gameObject;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.postSolve(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.postSolve(objA, contact, bNormal);
        }
    }

}
