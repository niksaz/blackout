package ru.spbau.blackout;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;

public class BlackoutContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        GameObject first = (GameObject) contact.getFixtureA().getBody().getUserData();
        GameObject second = (GameObject) contact.getFixtureB().getBody().getUserData();

        if (first.isDead() || second.isDead()) {
            return;
        }

        boolean firstAbility = first instanceof AbilityObject;
        boolean secondAbility = second instanceof AbilityObject;

        if (firstAbility && secondAbility) {
            // TODO: handle ability collisions
            first.kill();
            second.kill();
        } else if (firstAbility) {
            ((AbilityObject) first).beginContact(second);
        } else if (secondAbility) {
            ((AbilityObject) second).beginContact(first);
        }
    }

    @Override
    public void endContact(Contact contact) {
        // TODO
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
