package ru.spbau.blackout.rooms;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.entities.GameUnit;

public class TestingRoom extends GameRoom {
    public String map;
    public Array<GameUnit> units = new Array<GameUnit>();

    public String getMap() {
        return map;
    }

    public Array<GameUnit> getUnits() {
        return units;
    }
}
