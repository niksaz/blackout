package ru.spbau.blackout.abilities;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.Character;

public class ForceBlast extends SimpleInstantAbility {

    public ForceBlast(Definition def, Character character) {
        super(def, character);
    }

    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);
        getCharacter().kill();
    }

    public static class Definition extends Ability.Definition {

        private static final long serialVersionUID = 1000000000L;

        private static final String ICON_PATH = "abilities/force_blast/icon.png";
        private static final String NAME = "Force Blast";
        private static final float MAX_CHARGE_TIME = 1.5f;

        public Definition(int level) {
            super(level);
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
            return new ForceBlast(this, character);
        }
    }
}
