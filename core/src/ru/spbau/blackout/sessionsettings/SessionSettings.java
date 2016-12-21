package ru.spbau.blackout.sessionsettings;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.shapescreators.CircleCreator;


/**
 * Contains information about a game session.
 */
public class SessionSettings {

    // FIXME: make fields private?
    public String mapPath;
    public List<GameObject.Definition> definitions = new ArrayList<>();
    public long playerUid;
    public List<InitialState> initialStates = new ArrayList<>();


    public String getMapPath() { return mapPath; }

    public List<GameObject.Definition> getDefinitions() { return definitions; }

    public long getPlayerUid() { return playerUid; }

    public List<InitialState> getInitialStates() { return initialStates; }


    public static SessionSettings getTest() {
        SessionSettings session = new SessionSettings();
        session.mapPath =  "maps/duel/duel.g3db";

        Character.Definition hero = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                new Ability[] { new FireballAbility(7) },
                200
        );
        hero.overHeadPivotOffset.set(0, 0, 3.5f);
        session.definitions.add(hero);

        Character.Definition enemy = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                new Ability[] { new FireballAbility(2) },
                200
        );
        enemy.overHeadPivotOffset.set(0, 0, 3.5f);
        session.definitions.add(enemy);

        GameObject.Definition stone = new Decoration.Definition("models/stone/stone.g3db", new CircleCreator(1.1f));
        session.definitions.add(stone);

        session.playerUid = 0;

        session.initialStates.add(new InitialState(0, 0, 0));
        session.initialStates.add(new InitialState(1, 5, 5));
        session.initialStates.add(new InitialState(2, -5, 0));

        return session;
    }


    public static class InitialState {
        public int defId;
        public final Vector2 initialPosition = new Vector2();

        public InitialState(int defId, float initialX, float initialY) {
            this.defId = defId;
            this.initialPosition.set(initialX, initialY);
        }
    }
}
