package ru.spbau.blackout;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import ru.spbau.blackout.abilities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;

public class BlackoutContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        GameObject first = (GameObject) contact.getFixtureA().getBody().getUserData();
        GameObject second = (GameObject) contact.getFixtureB().getBody().getUserData();

        if (first.isDead() || second.isDead()) {
            return;
        }

        if (first instanceof AbilityObject) {
            ((AbilityObject) first).beginContact(second);
        }
        if (second instanceof AbilityObject) {
            ((AbilityObject) second).beginContact(first);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject first = (GameObject) contact.getFixtureA().getBody().getUserData();
        GameObject second = (GameObject) contact.getFixtureB().getBody().getUserData();

        if (first.isDead() || second.isDead()) {
            return;
        }

        if (first instanceof AbilityObject) {
            ((AbilityObject) first).endContact(second);
        }
        if (second instanceof AbilityObject) {
            ((AbilityObject) second).endContact(first);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
