package ru.spbau.blackout.rooms;

import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

/**
 * Contains settings for particular game session.
 */
public interface GameSessionSettings {
    String getMap();
    Array<GameObject.Definition> getObjectDefs();
    Hero.Definition getCharacter();
}
