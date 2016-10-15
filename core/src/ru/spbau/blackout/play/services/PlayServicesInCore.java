package ru.spbau.blackout.play.services;

import com.badlogic.gdx.utils.Array;

public class PlayServicesInCore implements PlayServicesListener {

    private static PlayServicesInCore listener;

    private PlayServices playServices;
    private BlackoutSnapshot snapshot;
    private final Array<CorePlayServicesListener> listeners = new Array<CorePlayServicesListener>();

    private PlayServicesInCore() {}

    public static PlayServicesInCore getInstance() {
        if (listener == null) {
            listener = new PlayServicesInCore();
        }
        return listener;
    }

    public void initialize(PlayServices playServices) {
        this.playServices = playServices;
        this.snapshot = null;
    }

    public PlayServices getPlayServices() {
        return playServices;
    }

    public BlackoutSnapshot getSnapshot() {
        return snapshot;
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
        this.snapshot = snapshot;
        for (CorePlayServicesListener listener : listeners) {
            listener.finishedLoadingSnapshot();
        }
    }

}
