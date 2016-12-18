package ru.spbau.blackout.game_session;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.shapescreators.CircleCreator;

public class TestingSessionSettings implements SessionSettings {
    public String map;
    public List<GameObject.Definition> objectDefs = new ArrayList<>();
    public Character.Definition character;
    public long playerUid;

    @Override
    public String getMap() {
        return map;
    }

    @Override
    public List<GameObject.Definition> getDefintions() {
        return objectDefs;
    }

    @Override
    public long getPlayerUid() {
        return playerUid;
    }

    //@Override
    public Character.Definition getCharacter() {
        return character;
    }

    public static TestingSessionSettings getTest() {
        TestingSessionSettings session = new TestingSessionSettings();
        session.map =  "maps/duel/duel.g3db";

        Character.Definition hero = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                0, 0,
                new Ability[] { new FireballAbility(7) },
                200
        );
        hero.overHeadPivotOffset.set(0, 0, 3.5f);
        session.objectDefs.add(hero);
        session.character = hero;

        Character.Definition enemy = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                5, 0,
                new Ability[] { new FireballAbility(2) },
                200
        );
        enemy.overHeadPivotOffset.set(0, 0, 3.5f);
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
