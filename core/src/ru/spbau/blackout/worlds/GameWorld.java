package ru.spbau.blackout.worlds;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.spbau.blackout.BlackoutContactListener;
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
 * <br>There is one more method called <code>updateState</code>. It must update things which are not connected
 * with physic driver. This method called one time per frame (i.e. without fixed step).
 */
public abstract class GameWorld {

    public static final int VELOCITY_ITERATIONS = 1;
    public static final int POSITION_ITERATIONS = 2;

    private final List<GameObject> gameObjects = new LinkedList<>();
    protected long stepNumber = 0;
    transient protected final World box2dWorld;  // FIXME: should be only on server


    public GameWorld() {
        // without gravity, without sleeping
        this.box2dWorld = new World(Vector2.Zero, false);
        this.box2dWorld.setContactListener(new BlackoutContactListener());
    }


    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void update(float delta) {
        /// common things
    }

    public Body addObject(GameObject object, BodyDef bodyDef) {
        this.gameObjects.add(object);
        return box2dWorld.createBody(bodyDef);
    }

    public void dispose() {
        this.box2dWorld.dispose();
        foreach (getGameObjects(), GameObject::dispose);
    }
}

