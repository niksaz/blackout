package ru.spbau.blackout.worlds;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GameWorld which is used on client side of multi-player game. It receives a serialized version of the world from the
 * server and updates its own.
 */
public class GameWorldWithExternalSerial extends GameWorld {

    private final AtomicReference<ObjectInputStream> externalWorldStream = new AtomicReference<>();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (externalWorldStream.get() != null) {
            try {
                inplaceDeserialize(externalWorldStream.getAndSet(null));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setExternalWorldStream(ObjectInputStream externalWorldStream) {
        this.externalWorldStream.set(externalWorldStream);
    }
}
