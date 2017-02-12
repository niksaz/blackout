package ru.spbau.blackout.abilities.gravity;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.simpleshell.SimpleShellAbility;
import ru.spbau.blackout.entities.Character;

public class GravityAbility extends SimpleShellAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EFFECT_PATH = "effects/force_blast/force_blast.pfx";
    static final String ICON_PATH = "abilities/force_blast/icon.png";
    static final String NAME = "Gravity";
    static final float MAX_CHARGE_TIME = 3f;
    static final float SHELL_RADIUS = 2.5f;
    static final float SHELL_MASS = 1000f;
    static final float DISTANCE = 10f;
    static final float SHELL_START_SPEED = 30f;
    static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    static final float BASE_MAX_DAMAGE = 1.5f;
    static final float MAX_DAMAGE_PER_LEVEL = 1f;
    static final float BASE_FORCE = 1.5f;
    static final float FORCE_PER_LEVEL = 1;

    protected GravityAbility(Definition def, Character character) {
        super(def, character);
    }

    public static class Definition extends SimpleShellAbility.Definition {

        public Definition(int level) {
            super(level, new GravityObject.Definition());
        }

        @Override
        public void setLevel(int newLevel) {
            super.setLevel(newLevel);
            ((GravityObject.Definition) shellDef).setForce(BASE_FORCE + FORCE_PER_LEVEL * getLevel());
        }

        @Override
        public Ability makeInstance(Character character) {
            return new GravityAbility(this, character);
        }

        @Override
        protected float startSpeed() { return SHELL_START_SPEED; }
        @Override
        protected float baseDamage() { return BASE_MAX_DAMAGE; }
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
