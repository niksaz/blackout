package ru.spbau.blackout.rooms;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;

public class TestingRoom extends GameRoom {
    public String map;
    public Array<GameUnit> units = new Array<GameUnit>();
    public Hero hero;

    public String getMap() {
        return map;
    }

    public Array<GameUnit> getUnits() {
        return units;
    }

    public Hero getHero() {
        return hero;
    }
}
