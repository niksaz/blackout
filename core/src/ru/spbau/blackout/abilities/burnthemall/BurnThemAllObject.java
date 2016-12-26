/*
package ru.spbau.blackout.abilities.burnthemall;

import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;

import static ru.spbau.blackout.abilities.burnthemall.BurnThemAllAbility.RADIUS;


public class BurnThemAllObject extends AbilityObject {

    private static final String CAST_SOUND_PATH = null;

    protected BurnThemAllObject(Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
    }


    public static class Definition extends AbilityObject.Definition {

        public Definition() {
            super(null, new CircleCreator(RADIUS), null, CAST_SOUND_PATH);
        }

        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            return new BurnThemAllObject(this, uid, x, y);
        }
    }
}
*/
