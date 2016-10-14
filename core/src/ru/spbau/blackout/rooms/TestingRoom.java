package ru.spbau.blackout.rooms;

import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

public class TestingRoom extends GameRoom {
    public String map;
    public Array<GameObject> objects = new Array<GameObject>();
    public Hero hero;

    @Override
    public String getMap() {
        return map;
    }

    @Override
    public Array<GameObject> getObjects() {
        return objects;
    }

    @Override
    public Hero getHero() {
        return hero;
    }
}
