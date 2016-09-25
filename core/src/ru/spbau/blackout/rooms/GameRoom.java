package ru.spbau.blackout.rooms;

/**
 * Contains settings for particular game session.
 */
public abstract class GameRoom {
    protected String map;
    protected int teams;
    protected int players;

    public void setMap(String map) {
        this.map = map;
    }

    public String getMap() {
        return map;
    }
}
