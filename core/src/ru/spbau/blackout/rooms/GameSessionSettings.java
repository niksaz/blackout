package ru.spbau.blackout.rooms;

import java.io.Serializable;
import java.util.List;

import java.io.Serializable;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

/**
 * Contains settings for particular game session.
 */
public interface GameSessionSettings extends Serializable {
    String getMap();
    List<GameObject.Definition> getObjectDefs();
    Hero.Definition getCharacter();
}
