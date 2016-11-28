package ru.spbau.blackout.network;

/**
 * Enum used for synchronization of a game phase between a server and a client.
 */
public enum GameState {
    WAITING,
    READY_TO_START,
    IN_PROCESS,
    FINISHED
}
