package ru.spbau.blackout.rooms;

import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

public class TestingRoom implements GameRoom {
    public String map;
    public Array<GameObject.Definition> objectDefs = new Array<>();
    public Hero.Definition character;

    @Override
    public String getMap() {
        return map;
    }

    @Override
    public Array<GameObject.Definition> getObjectDefs() {
        return objectDefs;
    }

    @Override
    public Hero.Definition getCharacter() {
        return character;
    }
}
