package ru.spbau.blackout.game_session;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.List;

import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;

/**
 * Contains settings for particular game session.
 */
public interface SessionSettings extends Serializable {

    // FIXME: map is more complicated thing than just a model
    /**
     * Returns a path to the map model.
     */
    String getMap();

    /**
     * Returns a list of definitions of all GameObjects in the game.
     */
    List<GameObject.Definition> getDefintions();
}
