package ru.spbau.blackout.rooms;

import com.badlogic.gdx.utils.Array;

import java.io.Serializable;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

/**
 * Contains settings for particular game session.
 */
public interface GameSessionSettings extends Serializable {
    String getMap();
    Array<GameObject.Definition> getObjectDefs();
    Hero.Definition getCharacter();
}
