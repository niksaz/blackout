package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;


public class FireballAbility extends SimpleInstantAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String FIRE_EFFECT_PATH = "abilities/fireball/particles/fireball.pfx";
    static final String EXPLOSION_EFFECT_PATH = "effects/small_explosion/small_explosion.pfx";
    /** The estimated distance of the ability in case of no external force. */
    private static final float DISTANCE = 12f;
    private static final float SHELL_START_SPEED = 30f;
    private static final String NAME = "Fireball";
    private static final String ICON_PATH = "abilities/fireball/icon.png";
    private static final float MAX_CHARGE_TIME = 1f;
    static final float SHELL_MASS = 5f;
    static final float IMPULSE_FACTOR = 40f;
    static final float SHELL_RADIUS = 0.5f;
    private static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    private static final float DAMAGE_INCREASE_PER_LEVEL = 5.0f;
    private static final float BASE_DAMAGE = 20.0f;

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
            super(level);
            shellDef = new FireballObject.Definition();
            shellDef.chestPivotOffset.set(0, 0, 1.5f);
            System.out.println("WTF " + shellDef.chestPivotOffset);
            shellDef.isSensor = true;
            shellDef.timeToLive = TIME_TO_LIVE;
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            shellDef.load(context);
        }

        @Override
        public void doneLoading(GameContext context) {
            super.doneLoading(context);
            shellDef.doneLoading();
        }

        @Override
        public Ability makeInstance(Character character) {
            // FIXME: it looks weird
            shellDef.damage = BASE_DAMAGE + DAMAGE_INCREASE_PER_LEVEL * getLevel();
            return new FireballAbility(this, character);
        }

        @Override
        public float getMaxChargeTime() {
            return MAX_CHARGE_TIME;
        }

        @Override
        public String getIconPath() {
            return ICON_PATH;
        }

        @Override
        public String getName() {
            return NAME;
        }
    }
}
