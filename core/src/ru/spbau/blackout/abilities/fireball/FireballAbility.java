package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;


public class FireballAbility extends SimpleInstantAbility {

    /** The estimated distance of the ability in case of no external force. */
    private static final float DISTANCE = 12f;
    private static final float SHELL_START_SPEED = 30f;


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

        private static final String ICON_PATH = "abilities/fireball/icon.png";
        private static final float MAX_CHARGE_TIME = 1f;
        private static final float SHELL_MASS = 5f;
        private static final float SHELL_RADIUS = 0.5f;
        private static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
        private static final float DAMAGE_INCREASE_PER_LEVEL = 5.0f;
        private static final float BASE_DAMAGE = 20.0f;

        private final FireballObject.Definition shellDef;


        public Definition(int level) {
            super(ICON_PATH, MAX_CHARGE_TIME, level);
            this.shellDef = new FireballObject.Definition(null, new CircleCreator(SHELL_RADIUS), SHELL_MASS);
            this.shellDef.chestPivotOffset.set(0, 0, 1.5f);
            this.shellDef.isSensor = true;
            this.shellDef.timeToLive = TIME_TO_LIVE;
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            this.shellDef.load(context);
        }

        @Override
        public void doneLoading(GameContext context) {
            super.doneLoading(context);
            this.shellDef.doneLoading();
        }

        @Override
        public Ability makeInstance(Character character) {
            // FIXME: it looks weird
            this.shellDef.damage = BASE_DAMAGE + DAMAGE_INCREASE_PER_LEVEL * getLevel();
            return new FireballAbility(this, character);
        }
    }
}
