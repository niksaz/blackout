package ru.spbau.blackout.abilities.fireball;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.simpleshell.SimpleShellAbility;
import ru.spbau.blackout.entities.Character;

public final class FireballAbility extends SimpleShellAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String FIRE_EFFECT_PATH = "abilities/fireball/particles/fireball.pfx";
    static final String EXPLOSION_EFFECT_PATH = "effects/small_explosion/small_explosion.pfx";
    /** The estimated distance of the ability in case of no external force. */
    static final float DISTANCE = 12f;
    static final float SHELL_START_SPEED = 30f;
    static final String NAME = "Fireball";
    static final String ICON_PATH = "abilities/fireball/icon.png";
    static final float MAX_CHARGE_TIME = 1f;
    static final float SHELL_MASS = 5f;
    static final float IMPULSE_FACTOR = 40f;
    static final float SHELL_RADIUS = 0.5f;
    static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    static final float DAMAGE_INCREASE_PER_LEVEL = 10.0f;
    static final float BASE_DAMAGE = 15.0f;

    protected FireballAbility(Definition def, Character character) {
        super(def, character);
    }

    public final static class Definition extends SimpleShellAbility.Definition {

        private static final long serialVersionUID = 1000000000L;

        public Definition(int level) {
            super(level, new FireballObject.Definition());
        }

        @Override
        public Ability makeInstance(Character character) {
            shellDef.damage = BASE_DAMAGE + DAMAGE_INCREASE_PER_LEVEL * getLevel();
            return new FireballAbility(this, character);
        }

        @Override
        public float maxChargeTime() { return MAX_CHARGE_TIME; }
        @Override
        public String name() { return NAME; }
        @Override
        public String iconPath() { return ICON_PATH; }
        @Override
        protected float startSpeed() { return SHELL_START_SPEED; }
    }
}
