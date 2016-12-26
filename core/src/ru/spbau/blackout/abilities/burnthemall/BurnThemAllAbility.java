/*

package ru.spbau.blackout.abilities.burnthemall;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Character;

public class BurnThemAllAbility extends SimpleInstantAbility {

    static final float MAX_CHARGE_TIME = 5f;
    static final String ICON_PATH = "abilities/burn_them_all/icon.png";
    static final String NAME = "Burn Them All";
    static final float RADIUS = 1.5f;
    static final float BASE_DAMAGE = 0.5f;
    static final float DAMAGE_INCREASE_PER_LEVEL = 0.2f;


    protected BurnThemAllAbility(Definition def, Character character) {
        super(def, character);
    }


    public static class Definition extends Ability.Definition {

        public Definition(int level) {
            super(level, ICON_PATH, NAME, MAX_CHARGE_TIME);
        }

        @Override
        public Ability makeInstance(Character character) {
            shellDef.damage = BASE_DAMAGE + DAMAGE_INCREASE_PER_LEVEL * getLevel();
            return new FireballAbility(this, character);
        }
    }
}
*/
