package ru.spbau.blackout.androidfeatures;

import com.badlogic.gdx.utils.Array;

public class PlayServicesInCore implements PlayServicesListener {

    private PlayServices playServices;
    private final Array<PlayServicesListener> listeners = new Array<>();

    public PlayServicesInCore(PlayServices playServices) {
        this.playServices = playServices;
    }

    public PlayServices getPlayServices() {
        return playServices;
    }

    @Override
    public void onSignInSucceeded() {
        for (PlayServicesListener listener : listeners) {
            listener.onSignInSucceeded();
        }
    }

    @Override
    public void onSignInFailed(String message) {
        for (PlayServicesListener listener : listeners) {
            listener.onSignInFailed(message);
        }
    }

    public void addListener(PlayServicesListener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(PlayServicesListener listener) {
        return listeners.removeValue(listener, true);
    }
}
