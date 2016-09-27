package ru.spbau.blackout.rooms;

import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.entities.GameUnit;

/**
 * Contains settings for particular game session.
 */
public abstract class GameRoom {
    public abstract String getMap();
    public abstract Array<GameUnit> getUnits();
}
