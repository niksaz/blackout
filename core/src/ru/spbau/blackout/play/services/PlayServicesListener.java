package ru.spbau.blackout.play.services;

import ru.spbau.blackout.play.services.BlackoutSnapshot;

public interface PlayServicesListener {
    void onSignInFailed();
    void onSignInSucceeded();

    void finishedLoadingSnapshot(BlackoutSnapshot snapshot);
}
