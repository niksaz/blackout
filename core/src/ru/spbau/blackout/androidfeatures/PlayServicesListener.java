package ru.spbau.blackout.androidfeatures;

public interface PlayServicesListener {
    void onSignInSucceeded();
    void onSignInFailed(String message);
}
