package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.abilities.AbilityObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;


public class FireballAbility extends SimpleInstantAbility {

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
    static final float DAMAGE_INCREASE_PER_LEVEL = 5.0f;
    static final float BASE_DAMAGE = 20.0f;

    protected FireballAbility(FireballAbility.Definition def, Character character) {
        super(def, character);
    }


    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);

        Vector2 unitPosition = getCharacter().getPosition();
        targetOffset.add(unitPosition);  // real target
        GameObject.Definition shellDef = ((Definition) getDef()).shellDef;
        AbilityObject shell = (AbilityObject) shellDef.makeInstanceWithNextUid(targetOffset);
        targetOffset.sub(unitPosition);

        targetOffset.scl(SHELL_START_SPEED / targetOffset.len());  // start speed
        shell.velocity.add(targetOffset);
    }


    public static class Definition extends Ability.Definition {

        private static final long serialVersionUID = 1000000000L;

        private final FireballObject.Definition shellDef;

        public Definition(int level) {
            super(level, ICON_PATH, NAME, MAX_CHARGE_TIME);
            shellDef = new FireballObject.Definition();
            shellDef.chestPivotOffset.set(0, 0, 1.5f);
            System.out.println("WTF " + shellDef.chestPivotOffset);
            shellDef.isSensor = true;
            shellDef.timeToLive = TIME_TO_LIVE;
        }

        @Override
        public Ability makeInstance(Character character) {
            shellDef.damage = BASE_DAMAGE + DAMAGE_INCREASE_PER_LEVEL * getLevel();
            return new FireballAbility(this, character);
        }
    }
}
