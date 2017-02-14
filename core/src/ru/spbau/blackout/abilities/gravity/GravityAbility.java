package ru.spbau.blackout.abilities.gravity;

import java.io.Serializable;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.simpleshell.SimpleShellAbility;
import ru.spbau.blackout.entities.Character;

public class GravityAbility extends SimpleShellAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EFFECT_PATH = "abilities/fireball/particles/fireball.pfx";;
    static final String ICON_PATH = "abilities/force_blast/icon.png";
    static final String NAME = "Gravity";
    static final float MAX_CHARGE_TIME = 3f;
    static final float SHELL_MASS = 1000;
    static final float DISTANCE = 10f;
    static final float SHELL_START_SPEED = 15;
    static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    static final float MAX_DAMAGE_BASE = 0.75f;
    static final float MAX_DAMAGE_PER_LEVEL = 0.3f;
    static final float MAX_FORCE_BASE = 750;
    static final float MAX_FORCE_PER_LEVEL = 300;
    static final float RADIUS = 5;


    protected GravityAbility(Definition def, Character character) {
        super(def, character);
    }

    public static class Definition extends SimpleShellAbility.Definition implements Serializable {

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
