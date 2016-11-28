package ru.spbau.blackout.gamesession;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.shapescreators.CircleCreator;

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

    public static TestingSessionSettings getTest() {
        TestingSessionSettings session = new TestingSessionSettings();
        session.map =  "maps/duel/duel.g3db";

        Hero.Definition hero = new Hero.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                0, 0,
                new Ability[] { new FireballAbility(7) }
        );
        session.objectDefs.add(hero);
        session.character = hero;

        Hero.Definition enemy = new Hero.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                5, 0,
                new Ability[] { new FireballAbility(2) }
        );
        session.objectDefs.add(enemy);

        GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db",
                new CircleCreator(1.1f),
                0, -20
        );
        session.objectDefs.add(stone);

        return session;
    }
}
