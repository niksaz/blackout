package ru.spbau.blackout.abilities.forceblast;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.abilities.fireball.FireballObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.shapescreators.CircleCreator;

public class ForceBlastAbility extends SimpleInstantAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EXPLOSION_EFFECT_PATH = "effects/small_explosion/small_explosion.pfx";
    static final String ICON_PATH = "abilities/force_blast/icon.png";
    static final String NAME = "Force Blast";
    static final float MAX_CHARGE_TIME = 1.5f;
    static final float RADIUS = 3f;
    static final float IMPULSE = 1000f;
    static final float DAMAGE = 20f;


    public ForceBlastAbility(Definition def, Character character) {
        super(def, character);
    }

    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);
        ((Definition) getDef()).shellDef.makeInstanceWithNextUid(getCharacter().getPosition());
    }

    public static class Definition extends Ability.Definition {

        private static final long serialVersionUID = 1000000000L;

        private final ForceBlastObject.Definition shellDef;

        public Definition(int level) {
            super(level);
            shellDef = new ForceBlastObject.Definition();
            shellDef.isSensor = true;
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

        @Override
        public Ability makeInstance(Character character) {
            return new ForceBlastAbility(this, character);
        }
    }
}
