package ru.spbau.blackout.play.services;

import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.BlackoutGame;

public class PlayServicesListenerInCore implements PlayServicesListener {

    private static final String TAG = "PlayServicesListenerInCore";

    private static PlayServicesListenerInCore listener;

    private BlackoutGame game;
    private final Array<CorePlayServicesListener> listeners = new Array<CorePlayServicesListener>();

    private PlayServicesListenerInCore() {}

    public static PlayServicesListenerInCore getInstance() {
        if (listener == null) {
            listener = new PlayServicesListenerInCore();
        }
        return listener;
    }

    public void initialize(BlackoutGame game) {
        this.game = game;
    }

    @Override
    public void onSignInFailed() {
        for (CorePlayServicesListener listener : listeners) {
            listener.onSignInFailed();
        }
    }

    @Override
    public void onSignInSucceeded() {
        for (CorePlayServicesListener listener : listeners) {
            listener.onSignInSucceeded();
        }
    }

    public void addListener(CorePlayServicesListener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(CorePlayServicesListener listener) {
        return listeners.removeValue(listener, true);
    }

    @Override
    public void finishedLoadingSnapshot(BlackoutSnapshot snapshot) {
        game.setSnapshot(snapshot);
        for (CorePlayServicesListener listener : listeners) {
            listener.finishedLoadingSnapshot();
        }
    }


}
