package ru.spbau.blackout.play.services;

public interface PlayServicesListener {
    void onSignInFailed();
    void onSignInSucceeded();

    void finishedLoadingSnapshot(BlackoutSnapshot snapshot);
}
