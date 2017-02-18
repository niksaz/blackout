package ru.spbau.blackout.worlds;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.spbau.blackout.BlackoutContactListener;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.Uid;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Main class for all in-game computation including physics.
 * Isn't connected with any graphics or audio in order to be able to be run on server.
 * Also used to send information about the game state from the server to clients.
 * Due to box2d limitations there is a complex system around it based on two steps.
 *
 * <p>
 *     Each DynamicObject has its natural velocity and temporary velocity.
 *
 *     <br>Natural velocity is affected by collisions and friction.
 *
 *     <br>Temporary velocity is reset to zero after each frame.
 *     It's useful when one needs to recalculate its influence on an object every frame
 *     (for example: character movement and Gravity ability)
 *
 *     <br>{@link GameObject::updateState} is a perfect place to update things which are not related with
 *     velocity (for example: character's HP).
 *
 *     <br>The first step is when natural velocity is processed.
 *     {@link GameObject::updateBeforeFirstStep} is a perfect place to apply impulses
 *     (although it's allowed to apply them at any time).
 *     This function is called on each <code>GameObject</code> right before the first step processing.
 *
 *     <br>Second step is when temporary velocity is processed.
 *     {@link GameObject::updateBeforeSecondStep} is a perfect place to apply temporary impulses
 *     (although it's allowed to apply them at any time).
 *     This function is called on each <code>GameObject</code> right before the second step processing.
 * </p>
 */
public abstract class GameWorld {

    private final Map<Uid, GameObject> gameObjectsMap = new HashMap<>();
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

    public final GameObject getObjectById(Uid uid) {
        return gameObjectsMap.get(uid);
    }

    public final boolean hasObjectWithId(Uid uid) {
        return gameObjectsMap.containsKey(uid);
    }

    public abstract void updatePhysics(float delta);

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
        foreach(getGameObjects(), GameObject::dispose);
        foreach(getDefinitions(), GameObject.Definition::dispose);
    }

    public void updateGraphic(float delta) {
        foreach(getGameObjects(), object -> object.updateGraphics(delta));
    }

    protected void removeDeadObject(GameObject object) {
        object.destroyBody(this.box2dWorld);
    }
}
