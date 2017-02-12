/*
package ru.spbau.blackout.abilities.gravity;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.DynamicAbilityObject;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;

public class GravityAbility extends SimpleInstantAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EFFECT_PATH = "effects/force_blast/force_blast.pfx";
    static final String ICON_PATH = "abilities/force_blast/icon.png";
    static final String NAME = "Gravity";
    static final float MAX_CHARGE_TIME = 3f;
    static final float RADIUS = 2.5f;
    static final float DISTANCE = 10f;
    static final float BASE_MAX_DAMAGE = 1.5f;
    static final float MAX_DAMAGE_PER_LEVEL = 1f;
    static final float BASE_FORCE = 1.5f;
    static final float FORCE_PER_LEVEL = 1;

    protected GravityAbility(Definition def, Character character) {
        super(def, character);
    }

    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);

        Vector2 unitPosition = getCharacter().getPosition();
        targetOffset.add(unitPosition);  // real target
        GameObject.Definition shellDef = ((FireballAbility.Definition) getDef()).shellDef;
        DynamicAbilityObject shell = (DynamicAbilityObject) shellDef.makeInstanceWithNextUid(targetOffset);
        targetOffset.sub(unitPosition);

        targetOffset.scl(SHELL_START_SPEED / targetOffset.len());  // start speed
        shell.velocity.add(targetOffset);
    }

    public static class Definition extends SimpleInstantAbility.Definition {

        public Definition(int level) {
            super(level, ICON_PATH, NAME, MAX_CHARGE_TIME);
        }

        @Override
        public void doneLoading(GameContext context) {
            super.doneLoading(context);
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
        }

        @Override
        public Ability makeInstance(Character character) {
            return new GravityAbility(this, character);
        }
    }
}
*/
