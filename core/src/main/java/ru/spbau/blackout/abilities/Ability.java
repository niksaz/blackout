package ru.spbau.blackout.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.Serializable;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.network.UIServer;
import ru.spbau.blackout.serializationutils.EfficientInputStream;
import ru.spbau.blackout.serializationutils.EfficientOutputStream;
import ru.spbau.blackout.serializationutils.HasState;


/**
 * Abstract class for ability in-game representation.
 * Each character has its own instance of each its ability.
 */
public abstract class Ability implements HasState {

    private float chargeTime;
    private final Character character;
    private Ability.Definition def;

    protected Ability(Ability.Definition def, Character character) {
        this.def = def;
        this.character = character;
    }

    @Override
    public void getState(EfficientOutputStream out) throws IOException {
        out.writeFloat(chargeTime);
    }

    @Override
    public void setState(EfficientInputStream in) throws IOException {
        setChargeTime(in.readFloat());
    }

    public final Ability.Definition getDef() {
        return def;
    }

    /** Called once when a touch goes down on the icon. */
    public abstract void onCastStart(UIServer server);
    /** Called each frame when the icon is pressed. */
    public abstract void onCastEnd(UIServer server);
    /** Called once when a touch goes up. */
    public abstract void inCast(UIServer server, float delta);

    public void cast(Vector2 targetOffset) {
        chargeStart();
    }

    public void chargeUpdate(float deltaTime) {
        chargeTime -= Math.min(deltaTime, chargeTime);
    }

    public Character getCharacter() { return character; }

    public float getChargeTime() { return chargeTime; }

    public void setChargeTime(float chargeTime) { this.chargeTime = chargeTime; }

    /**
     * Sets current chargeUpdate time as <code>maxChargeTime</code>.
     */
    public void chargeStart() {
        setChargeTime(def.maxChargeTime());
    }


    public static abstract class Definition implements Serializable {

        private static final long serialVersionUID = 1000000000L;

        private int level;
        private /*final*/ transient GameContext context;

        public Definition(int level) {
            this.level = level;
        }

        /** Load necessary assets. */
        public void load(GameContext context) {
            // loading of icon has to be here because it isn't accessible from `AbilityIcon` class in the loading stage
            context.getAssets().load(iconPath(), Texture.class);
        }

        public void doneLoading(GameContext context) {
            this.context = context;
        }

        public final GameContext getContext() {
            return context;
        }

        public final int getLevel() {
            return level;
        }

        public final void setLevel(int newLevel) {
            level = newLevel;
        }

        /**
         * Update all the stuff which is related with the level of the ability.
         */
        public void updateLevel() {}

        protected abstract Ability makeInstanceImpl(Character character);

        public final Ability makeInstance(Character character) {
            updateLevel();
            return makeInstanceImpl(character);
        }

        public final void increaseLevel() {
            setLevel(getLevel() + 1);
        }

        public abstract float maxChargeTime();
        public abstract String name();
        public abstract String iconPath();
    }
}
