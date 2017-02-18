package ru.spbau.blackout.abilities.simpleshell;

import com.badlogic.gdx.math.Vector2;

import org.jetbrains.annotations.Contract;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.abilities.DynamicAbilityObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;

public abstract class SimpleShellAbility extends SimpleInstantAbility {

    protected SimpleShellAbility(Definition def, Character character) {
        super(def, character);
    }

    /**
     * Creates shell with start speed.
     */
    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);

        Vector2 unitPosition = getCharacter().getPosition();
        targetOffset.add(unitPosition);  // real target
        GameObject.Definition shellDef = ((Definition) getDef()).shellDef;
        DynamicAbilityObject shell = (DynamicAbilityObject) shellDef.makeInstanceWithNextUid(targetOffset);

        float startSpeed = ((Definition) getDef()).startSpeed();
        Vector2 startVelocity = targetOffset.sub(unitPosition).scl(startSpeed / targetOffset.len());
        shell.velocity.add(startVelocity);
    }

    public abstract static class Definition extends Ability.Definition {

        private static final long serialVersionUID = 1000000000L;

        protected final SimpleShellObject.Definition shellDef;

        public Definition(int level, SimpleShellObject.Definition shellDef) {
            super(level);
            this.shellDef = shellDef;
        }

        @Override
        public void updateLevel() {
            super.updateLevel();
            this.shellDef.damage = baseDamage() + damagePerLevel() * (getLevel() - 1);
        }

        @Contract(pure = true)
        protected abstract float startSpeed();
        @Contract(pure = true)
        protected abstract float baseDamage();
        @Contract(pure = true)
        protected abstract float damagePerLevel();
    }
}
