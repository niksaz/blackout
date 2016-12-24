package ru.spbau.blackout.worlds;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.spbau.blackout.BlackoutContactListener;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Main class for all in-game computation including physics.
 * Isn't connected with any graphics or audio in order to be able to be run on server.
 * Also used to send information about the game state from the server to clients.
 *
 *
 * <p>Physics system:
 * <br>Due to box2d limitations there is a complex system around it based on two steps.
 *
 * <br>Physic driver used with the fixed step (see WORLD_STEP static field) and without interpolation.
 *
 * <br>The first step is when external velocity is processed. In <code>updateForFirstStep</code> method
 * each object must set its external velocity as its body's velocity.
 *
 * <br>In <code>updateForSecondStep</code> method each object must getOriginal its new external velocity from
 * its body's velocity and then it must put its own velocity (just like <code>selfVelocity</code> of GameUnit) instead.
 * The resulting velocity of the second step isn't important.
 *
 * <br>There is one more method called <code>updateGraphics</code>. It must updateState things which are not connected
 * with physic driver. This method called one time per frame (i.e. without fixed step).
 */
public abstract class GameWorld {

    public static final int VELOCITY_ITERATIONS = 1;
    public static final int POSITION_ITERATIONS = 2;

    private final SortedMap<Long, GameObject> gameObjectsMap = new TreeMap<>();
    protected long stepNumber = 0;
    transient protected final World box2dWorld;  // FIXME: should be only on server
    private final List<GameObject.Definition> definitions;


    public GameWorld(List<GameObject.Definition> definitions) {
        // without gravity, without sleeping
        box2dWorld = new World(Vector2.Zero, false);
        box2dWorld.setContactListener(new BlackoutContactListener());
        this.definitions = definitions;
    }


    public final Collection<GameObject> getGameObjects() {
        return gameObjectsMap.values();
    }

    public final List<GameObject.Definition> getDefinitions() { return definitions; }

    public final GameObject getObjectById(long uid) {
        return gameObjectsMap.get(uid);
    }

    public final boolean hasObjectWithId(long uid) {
        return gameObjectsMap.containsKey(uid);
    }

    public abstract void updateState(float delta);

    protected void updateGraphics(float delta) {
    }

    public Body addObject(GameObject object, BodyDef bodyDef) {
        gameObjectsMap.put(object.getUid(), object);
        return box2dWorld.createBody(bodyDef);
    }

    public void load(GameContext context) {
        foreach(definitions, def -> def.load(context));
    }

    public void doneLoading() {
        foreach(definitions, GameObject.Definition::doneLoading);
    }

    public void dispose() {
        box2dWorld.dispose();
        foreach (getGameObjects(), GameObject::dispose);
    }

    public void updateGraphic(float delta) {
        foreach(getGameObjects(), object -> object.updateGraphics(delta));
    }
}
