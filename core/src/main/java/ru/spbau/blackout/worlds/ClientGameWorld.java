package ru.spbau.blackout.worlds;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.network.AndroidClient;
import ru.spbau.blackout.serializationutils.EfficientInputStream;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.utils.Uid;


/**
 * GameWorld which is used on client side of multi-player game. It receives a serialized stepNumber of the world from
 * the server and updates its own.
 */
public class ClientGameWorld extends GameWorld {

    private final AtomicReference<EfficientInputStream> externalWorldStream = new AtomicReference<>();
    private final AndroidClient clientNetworkThread;

    public ClientGameWorld(SessionSettings sessionSettings, AndroidClient clientNetworkThread) {
        super(sessionSettings.getDefinitions());
        this.clientNetworkThread = clientNetworkThread;
    }


    public void setState(EfficientInputStream in) throws IOException, ClassNotFoundException {
        long newStepNumber = in.readLong();

        if (newStepNumber > stepNumber) {
            stepNumber = newStepNumber;

            Set<Uid> updated = new HashSet<>();
            int length = in.readInt();
            for (int i = 0; i < length; i++) {
                Uid uid = in.readObject(Uid.class);
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
                    removeDeadObject(go);
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
    public void updatePhysics(float delta) {
        if (externalWorldStream.get() != null) {
            try {
                setState(externalWorldStream.getAndSet(null));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        updateGraphics(delta);
    }

    public void setExternalWorldStream(EfficientInputStream externalWorldStream) {
        this.externalWorldStream.set(externalWorldStream);
    }

    public void interruptClientNetworkThread() {
        clientNetworkThread.stop();
    }
}
