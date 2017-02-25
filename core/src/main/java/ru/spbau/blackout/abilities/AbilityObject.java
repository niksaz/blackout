package ru.spbau.blackout.abilities;

import ru.spbau.blackout.entities.GameObject;

public interface AbilityObject {

    /** Calls when the object contacts with another object. */
    void beginContact(GameObject object);
}
