package ru.spbau.blackout.gamesession;

import java.io.Serializable;
import java.util.List;

import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;

/**
 * Contains settings for particular game session.
 */
public interface GameSessionSettings extends Serializable {

    // FIXME: map is more complicated thing than just a model
    /**
     * Returns a path to the map model.
     */
    String getMap();

    /**
     * Returns a list of definitions of all GameObjects in the game.
     */
    List<GameObject.Definition> getObjectDefs();

    /**
     * Returns a definition of current player's character.
     * Note that it also must be represented in the common list of GameObjects
     */
    Character.Definition getCharacter();
}
