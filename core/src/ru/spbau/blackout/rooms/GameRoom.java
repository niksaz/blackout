package ru.spbau.blackout.rooms;

import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

/**
 * Contains settings for particular game session.
 */
public abstract class GameRoom {
    public abstract String getMap();
    public abstract Array<GameObject> getObjects();
    public abstract Hero getHero();
}
