package ru.spbau.blackout.abilities.forceblast;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.abilities.fireball.FireballObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;

public class ForceBlastAbility extends SimpleInstantAbility {

    static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    static final String EXPLOSION_EFFECT_PATH = "effects/force_blast/force_blast.pfx";
    static final String ICON_PATH = "abilities/force_blast/icon.png";
    static final String NAME = "Force Blast";
    static final float MAX_CHARGE_TIME = 1.5f;
    static final float RADIUS = 2.5f;
    static final float IMPULSE = 1200f;
    static final float BASE_DAMAGE = 15;
    static final float DAMAGE_INCREASE_PER_LEVEL = 3;


    protected ForceBlastAbility(Definition def, Character character) {
        super(def, character);
    }

    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);
        GameObject shell = ((Definition) getDef()).shellDef.makeInstanceWithNextUid(getCharacter().getPosition());
        ((ForceBlastObject) shell).setCaster(getCharacter());
    }

    public static class Definition extends Ability.Definition {

        private static final long serialVersionUID = 1000000000L;

        private final ForceBlastObject.Definition shellDef;

        public Definition(int level) {
            super(level, ICON_PATH, NAME, MAX_CHARGE_TIME);
            shellDef = new ForceBlastObject.Definition();
            shellDef.chestPivotOffset.set(0, 0, 1.5f);
            shellDef.isSensor = true;
        }

        @Override
        public Ability makeInstance(Character character) {
            shellDef.damage = BASE_DAMAGE + DAMAGE_INCREASE_PER_LEVEL * getLevel();
            return new ForceBlastAbility(this, character);
        }
    }
}
