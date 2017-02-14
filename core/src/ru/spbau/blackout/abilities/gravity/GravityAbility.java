package ru.spbau.blackout.abilities.gravity;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.simpleshell.SimpleShellAbility;
import ru.spbau.blackout.entities.Character;

public class GravityAbility extends SimpleShellAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EFFECT_PATH = "effects/gravity/gravity.pfx";;
    static final String ICON_PATH = "abilities/gravity/icon.png";
    static final String NAME = "Gravity";
    static final float MAX_CHARGE_TIME = 4f;
    static final float SHELL_MASS = 10000;
    static final float DISTANCE = 12f;
    static final float SHELL_START_SPEED = 15;
    static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    static final float MAX_DAMAGE_BASE = 0.5f;
    static final float MAX_DAMAGE_PER_LEVEL = 0.2f;
    static final float MAX_FORCE_BASE = 750;
    static final float MAX_FORCE_PER_LEVEL = 300;
    static final float RADIUS = 7;

    protected GravityAbility(Definition def, Character character) {
        super(def, character);
    }

    public static class Definition extends SimpleShellAbility.Definition {

        public Definition(int level) {
            super(level, new GravityObject.Definition());
        }

        @Override
        public void updateLevel() {
            super.updateLevel();
            ((GravityObject.Definition) shellDef).maxForce = MAX_FORCE_BASE + MAX_FORCE_PER_LEVEL * (getLevel() - 1);
        }

        @Override
        protected Ability makeInstanceImpl(Character character) {
            ((GravityObject.Definition) shellDef).caster = character;
            return new GravityAbility(this, character);
        }

        @Override
        protected float startSpeed() { return SHELL_START_SPEED; }
        @Override
        protected float baseDamage() { return MAX_DAMAGE_BASE; }
        @Override
        protected float damagePerLevel() { return MAX_DAMAGE_PER_LEVEL; }
        @Override
        public float maxChargeTime() { return MAX_CHARGE_TIME; }
        @Override
        public String name() { return NAME; }
        @Override
        public String iconPath() { return ICON_PATH; }
    }
}
