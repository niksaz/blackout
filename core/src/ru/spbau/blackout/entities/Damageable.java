package ru.spbau.blackout.entities;


/**
 * Interface for an object which has health and can be damaged (e.g. by an ability).
 *
 * <p> Warning:
 * A minimal complete definition must include implementations of <code>getHeath</code>
 * and either <code>damage</code> or <code>setHealth</code>.
 * <br>If neither of the last two methods is overridden, the infinite recursion will occur.
 */
public interface Damageable {
    float getHealth();

    default void damage(float damage) {
        this.setHealth(this.getHealth() - damage);
    }

    default void setHealth(float newHealth) {
        this.damage(this.getHealth() - newHealth);
    }
}
