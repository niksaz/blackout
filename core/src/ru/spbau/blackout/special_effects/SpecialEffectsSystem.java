package ru.spbau.blackout.special_effects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class SpecialEffectsSystem {
    private final Set<SpecialEffect> effects = new HashSet<>();


    public void add(SpecialEffect effect) {
        this.effects.add(effect);
    }

    public void remove(SpecialEffect effect) {
        this.effects.remove(effect);
    }


    public void update(float deltaTime) {
        for (Iterator<SpecialEffect> it = this.effects.iterator(); it.hasNext();) {
            SpecialEffect effect = it.next();
            if (!effect.update(deltaTime)) {
                it.remove();
            }
        }
    }
}
