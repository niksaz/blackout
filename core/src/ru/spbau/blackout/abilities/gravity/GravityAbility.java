package ru.spbau.blackout.abilities.gravity;

<<<<<<< HEAD
=======
import ru.spbau.blackout.GameContext;
>>>>>>> 3749634... Gravity first version implemented
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.simpleshell.SimpleShellAbility;
import ru.spbau.blackout.entities.Character;

public class GravityAbility extends SimpleShellAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EFFECT_PATH = "abilities/fireball/particles/fireball.pfx";;
    static final String ICON_PATH = "abilities/force_blast/icon.png";
    static final String NAME = "Gravity";
    static final float MAX_CHARGE_TIME = 3f;
<<<<<<< HEAD
    static final float SHELL_MASS = 1000;
    static final float DISTANCE = 10f;
    static final float SHELL_START_SPEED = 20;
    static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    static final float MAX_DAMAGE_BASE = 0.12f;
    static final float MAX_DAMAGE_PER_LEVEL = 0.05f;
    static final float MAX_FORCE_BASE = 150;
    static final float MAX_FORCE_PER_LEVEL = 60;
    static final float RADIUS = 4f;
=======
    static final float SHELL_RADIUS = 2.5f;
    static final float SHELL_MASS = 1000f;
    static final float DISTANCE = 10f;
    static final float SHELL_START_SPEED = 30f;
    static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    static final float BASE_MAX_DAMAGE = 1.5f;
    static final float MAX_DAMAGE_PER_LEVEL = 1f;
    static final float BASE_FORCE = 1.5f;
    static final float FORCE_PER_LEVEL = 1;
>>>>>>> 3749634... Gravity first version implemented

    protected GravityAbility(Definition def, Character character) {
        super(def, character);
    }

    public static class Definition extends SimpleShellAbility.Definition {

        public Definition(int level) {
            super(level, new GravityObject.Definition());
<<<<<<< HEAD
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
=======
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
>>>>>>> 3749634... Gravity first version implemented
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
