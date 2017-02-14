package ru.spbau.blackout.abilities.forceblast;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.abilities.fireball.FireballObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;

public final class ForceBlastAbility extends SimpleInstantAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EXPLOSION_EFFECT_PATH = "effects/small_explosion/small_explosion.pfx";
    static final String ICON_PATH = "abilities/force_blast/icon.png";
    static final String NAME = "Force Blast";
    static final float MAX_CHARGE_TIME = 1.5f;
    static final float RADIUS = 2.5f;
    static final float IMPULSE = 1200f;
    static final float BASE_DAMAGE = 15;
    static final float DAMAGE_INCREASE_PER_LEVEL = 3;
    static final float EXPLOSION_TIME = 0.2f;


    protected ForceBlastAbility(Definition def, Character character) {
        super(def, character);
    }

    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);
        ForceBlastObject shell = (ForceBlastObject)
                ((Definition) getDef()).shellDef.makeInstanceWithNextUid(getCharacter().getPosition());
        shell.setCaster(getCharacter());
    }

    public final static class Definition extends Ability.Definition {

        private static final long serialVersionUID = 1000000000L;

        private final ForceBlastObject.Definition shellDef;

        public Definition(int level) {
            super(level);
            shellDef = new ForceBlastObject.Definition();
            shellDef.chestPivotOffset.set(0, 0, 1.5f);
            shellDef.isSensor = true;
        }

        @Override
        public void updateLevel() {
            super.updateLevel();
            shellDef.damage = BASE_DAMAGE + DAMAGE_INCREASE_PER_LEVEL * (getLevel() - 1);
        }

        @Override
        protected Ability makeInstanceImpl(Character character) {
            return new ForceBlastAbility(this, character);
        }

        @Override
        public float maxChargeTime() { return MAX_CHARGE_TIME; }
        @Override
        public String name() { return NAME; }
        @Override
        public String iconPath() { return ICON_PATH; }
    }
}
