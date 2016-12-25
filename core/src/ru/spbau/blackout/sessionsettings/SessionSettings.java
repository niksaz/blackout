package ru.spbau.blackout.sessionsettings;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Finder;


/**
 * Contains information about a game session.
 *
 * <p>WARNING: There is a harmless bug, which I don't want to fix.
 * If a definition contains reference to another one, you won't be able to
 * add both of them as initial objects.
 */
public final class SessionSettings implements Serializable {

    private final String mapPath;
    private final List<GameObject.Definition> definitions = new ArrayList<>();
    private long playerUid = 0;
    private final List<InitialState> initialStates = new ArrayList<>();
    private transient Finder<GameObject.Definition> finder = new Finder<>(GameObject.Definition.class, definitions);
    private transient long lastUid = 0;

    public SessionSettings(String mapPath) {
        this.mapPath = mapPath;
    }

    public String getMapPath() { return mapPath; }
    public List<GameObject.Definition> getDefinitions() { return definitions; }
    public void setPlayerUid(long uid) { this.playerUid = uid; }
    public long getPlayerUid() { return playerUid; }

    public void initializeGameWorld() {
        for (InitialState state : initialStates) {
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
    public int addInitialObject(GameObject.Definition def, float initialX, float initialY) {
        if (finder.getVisited().contains(def)) {
            throw new IllegalArgumentException("The definition is already added.");
        }

        int num = addDefinition(def);
        initialStates.add(new InitialState(num, initialX, initialY, getNextUid()));
        return num;
    }

    private long getNextUid() {
        lastUid += 1;
        return lastUid;
    }

    // FIXME: just for test
    public static SessionSettings createDefaultSession(Array<Character.Definition> characters) {
        final Array<Vector2> initialPositionsPool = new Array<>();
        initialPositionsPool.add(new Vector2(0, 10));
        initialPositionsPool.add(new Vector2(-5, 5));
        initialPositionsPool.add(new Vector2(5, 5));
        initialPositionsPool.add(new Vector2(0, 0));

        final SessionSettings session = new SessionSettings("maps/duel/duel.g3db");
        session.setPlayerUid(1);

        for (Character.Definition characterDefinition : characters) {
            if (initialPositionsPool.size == 0) {
                throw new IllegalStateException("Too much characters. Extend pool to place more characters");
            }
            final int index = MathUtils.random(initialPositionsPool.size - 1);
            final Vector2 position = initialPositionsPool.get(index);
            initialPositionsPool.removeIndex(index);

            characterDefinition.overHeadPivotOffset.set(0, 0, 3.5f);
            session.addInitialObject(characterDefinition, position.x, position.y);
        }

        final GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db",
                new CircleCreator(1.1f)
        );
        session.addInitialObject(stone, -5, 0);

        return session;
    }

    public long getLastUid() {
        return lastUid;
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
