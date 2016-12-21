package ru.spbau.blackout.sessionsettings;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Finder;

import static ru.spbau.blackout.utils.ReflectUtils.findAllImpls;


/**
 * Contains information about a game session.
 *
 * <p>WARNING: There is a harmless bug, which I don't want to fix.
 * If a definition contains reference to another one, you won't be able to
 * add both of them as initial objects.
 */
public final class SessionSettings implements Serializable {

    private String mapPath;
    private final List<GameObject.Definition> definitions = new ArrayList<>();
    private long playerUid = 0;
    private final List<InitialState> initialStates = new ArrayList<>();
    private transient Finder<GameObject.Definition> finder = new Finder<>(GameObject.Definition.class, definitions);

    public SessionSettings(String mapPath) {
        this.mapPath = mapPath;
    }


    public String getMapPath() { return mapPath; }
    public List<GameObject.Definition> getDefinitions() { return definitions; }
    public void setPlayerUid(long uid) { this.playerUid = uid; }
    public long getPlayerUid() { return playerUid; }

    public void initializeGameWorld() {
        for (InitialState state : initialStates) {
            System.out.println(state.uid);
            getDefinitions().get(state.defNum).makeInstance(state.uid, state.initialPosition);
        }
        for (int i = 0; i < definitions.size(); i++) {
            definitions.get(i).setDefNumber(i);
        }
    }

    /**
     * Adds definition to the list of possible definitions.
     *
     * <p>Note: In most cases you need {@link #addInitialObject} instead of this.
     */
    public int addDefinition(GameObject.Definition def) {
        int num = getDefinitions().size();
        finder.add(def);
        return num;
    }

    /**
     * An instance of the definition will appear in the given position in the beginning of the game.
     * The definition also will be added to the list of possible definitions.
     */
    public int addInitialObject(GameObject.Definition def, float initialX, float initialY, long uid) {
        if (finder.getVisited().contains(def)) {
            throw new IllegalArgumentException("The definition is already added.");
        }

        int num = addDefinition(def);
        initialStates.add(new InitialState(num, initialX, initialY, uid));
        return num;
    }


    // FIXME: just for test
    public static SessionSettings getTest() {
        SessionSettings session = new SessionSettings("maps/duel/duel.g3db");

        Character.Definition hero = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                new Ability[] { new FireballAbility(7) },
                200
        );
        hero.overHeadPivotOffset.set(0, 0, 3.5f);
        session.addInitialObject(hero, 0, 0, 1);
        session.setPlayerUid(1);

        Character.Definition enemy = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                new Ability[] { new FireballAbility(2) },
                200
        );
        enemy.overHeadPivotOffset.set(0, 0, 3.5f);
        session.addInitialObject(enemy, 5, 5, 2);

        GameObject.Definition stone = new Decoration.Definition("models/stone/stone.g3db", new CircleCreator(1.1f));
        session.addInitialObject(stone, -5, 0, 3);

        return session;
    }


    private static class InitialState implements Serializable {
        int defNum;
        final Vector2 initialPosition = new Vector2();
        long uid;

        public InitialState(int defNum, float initialX, float initialY, long uid) {
            this.defNum = defNum;
            this.initialPosition.set(initialX, initialY);
            this.uid = uid;
        }
    }
}
