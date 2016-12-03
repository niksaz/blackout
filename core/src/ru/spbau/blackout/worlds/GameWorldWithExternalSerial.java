package ru.spbau.blackout.worlds;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.entities.GameObject;

public class GameWorldWithExternalSerial extends GameWorld {

    private final AtomicReference<ObjectInputStream> externalWorldStream = new AtomicReference<>();

    @Override
    public void update(float delta) {
        if (externalWorldStream.get() != null) {
            try {
                inplaceDeserialize(externalWorldStream.getAndSet(null));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // TODO: whether this needed?
        for (GameObject object : gameObjects) {
            object.updateState(delta);
        }
    }

    public void setExternalWorldStream(ObjectInputStream externalWorldStream) {
        this.externalWorldStream.set(externalWorldStream);
    }
}
