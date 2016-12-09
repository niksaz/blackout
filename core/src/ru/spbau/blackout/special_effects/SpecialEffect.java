package ru.spbau.blackout.special_effects;


/**
 * See package level documentation.
 */
public interface SpecialEffect {
    /**
     * Updates effect and returns true if this effect hasn't finished yet.
     */
    boolean update(float deltaTime);
}
