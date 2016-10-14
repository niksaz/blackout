package ru.spbau.blackout.play.services;

public interface PlayServicesListener {
    void onSignInSucceeded();
    void finishedLoadingSnapshot(BlackoutSnapshot snapshot);
}
