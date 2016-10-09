package ru.spbau.blackout.play.services;

public interface CorePlayServicesListener {
    void onSignInFailed();
    void onSignInSucceeded();

    void finishedLoadingSnapshot();
}
