package ru.spbau.blackout.worlds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.screens.LoadScreen;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * GameWorld which is used on client side of multi-player game. It receives a serialized stepNumber of the world from
 * the server and updates its own.
 */
public class ClientGameWorld extends GameWorld {

    private final AtomicReference<ObjectInputStream> externalWorldStream = new AtomicReference<>();

    public ClientGameWorld(List<GameObject.Definition> definitions) {
        super(definitions);
    }


    public void setState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        long newStepNumber = in.readLong();

        if (newStepNumber > stepNumber) {
            stepNumber = newStepNumber;

            Set<Long> updated = new HashSet<>();
            int length = in.readInt();
            for (int i = 0; i < length; i++) {
                long uid = in.readLong();
                int defNumber = in.readInt();
                updated.add(uid);
                if (!hasObjectWithId(uid)) {
                    getDefinitions().get(defNumber).makeInstance(uid);
                }
                getObjectById(uid).setState(in);
            }

            for (Iterator<GameObject> it = getGameObjects().iterator(); it.hasNext();) {
                GameObject go = it.next();
                if (!updated.contains(go.getUid())) {
                    go.kill();
                    it.remove();
                }
            }
        }
    }

    @Override
    public void doneLoading() {
        super.doneLoading();
    }

    @Override
    public void updateState(float delta) {
        if (externalWorldStream.get() != null) {
            try {
                setState(externalWorldStream.getAndSet(null));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        updateGraphics(delta);
    }

    public void setExternalWorldStream(ObjectInputStream externalWorldStream) {
        this.externalWorldStream.set(externalWorldStream);
    }
}
