package ru.spbau.blackout.gamesession;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

public class TestingSessionSettings implements GameSessionSettings {
    public String map;
    public List<GameObject.Definition> objectDefs = new ArrayList<>();
    public Hero.Definition character;

    //@Override
    public String getMap() {
        return map;
    }

    @Override
    public List<GameObject.Definition> getObjectDefs() {
        return objectDefs;
    }

    //@Override
    public Hero.Definition getCharacter() {
        return character;
    }
}
