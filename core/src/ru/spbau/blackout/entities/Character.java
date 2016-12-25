package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.ForceBlast;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.graphiceffects.HealthBarEffect;
import ru.spbau.blackout.progressbar.HorizontalProgressBar;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Serializer;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;


public class Character extends GameUnit implements Damageable  {

    private final List<Ability> abilities = new ArrayList<>();
    private float health;
    private float maxHealth;


    public Character(Character.Definition def, long uid, float x, float y) {
        super(def, uid, x, y);

        for (Ability.Definition abilityDef : def.abilities) {
            abilities.add(abilityDef.makeInstance(this));
        }

        maxHealth = def.maxHealth;
        health = maxHealth;
    }

    public final Ability getAbility(int num) {
        return abilities.get(num);
    }

    public final List<Ability> getAbilities() {
        return abilities;
    }

    public void castAbility(int abilityNum, Vector2 target) {
        Ability ability = getAbility(abilityNum);
        ability.cast(target);
        // TODO: cast animation
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        for (Ability ability : abilities) {
            ability.chargeUpdate(delta);
        }
    }

    @Override
    public void getState(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        super.getState(out);
        out.writeFloat(health);
        for (Ability ability : abilities) {
            ability.getState(out);
        }
    }

    @Override
    public void setState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.setState(in);
        health = in.readFloat();
        for (Ability ability : abilities) {
            ability.setState(in);
        }
    }

    public int getAbilityNum(Ability ability) {
        for (int i = 0; i < abilities.size(); i++) {
            if (abilities.get(i) == ability) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void damage(float damage) {
        health -= damage;
        if (health <= 0) {
            kill();
        }
    }

    @Override
    public float getHealth() { return health; }

    public float getMaxHealth() { return maxHealth; }



    public static class Definition extends GameUnit.Definition {

        private static final long serialVersionUID = 1000000000L;

        private transient SimpleProgressBar healthBar;
        public Ability.Definition[] abilities;
        public float maxHealth;


        public Definition(String modelPath, Creator<Shape> shapeCreator,
                          Ability.Definition[] abilities, float maxHealth) {
            super(modelPath, shapeCreator);
            this.abilities = abilities;
            this.maxHealth = maxHealth;
        }

        @Override
        public void load(GameContext context) {
            super.load(context);

            healthBar = new HorizontalProgressBar(HealthBar.PATH_EMPTY, HealthBar.PATH_FULL);
            healthBar.load(context.getAssets());

            for (Ability.Definition abilityDef : abilities) {
                abilityDef.load(context);
            }
        }

        @Override
        public void doneLoading() {
            super.doneLoading();

            healthBar.doneLoading(context.getAssets());
            healthBar.setSize(HealthBar.WIDTH, HealthBar.HEIGHT);
            healthBar.toBack();

            for (Ability.Definition abilityDef : abilities) {
                abilityDef.doneLoading(context);
            }
        }

        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            Character character = new Character(this, uid, x, y);

            if (context.hasUI()) {
                SimpleProgressBar unitHb = healthBar.copy();
                context.getScreen().getUi().stage.addActor(unitHb);
                character.graphicEffects.add(new HealthBarEffect(character, unitHb, context));
            }

            return character;
        }

        public byte[] serializeToByteArray() {
            return Serializer.serializeToByteArray(this);
        }

        public static Definition deserializeFromByteArray(byte[] byteRepresentation) {
            return (Definition) Serializer.deserializeFromByteArray(byteRepresentation);
        }

        public static Definition createDefaultCharacterDefinition() {
            return new Character.Definition(
                    "models/wizard/wizard.g3db",
                    new CircleCreator(0.6f),
                    new Ability.Definition[] {
                        new FireballAbility.Definition(1),
                        new ForceBlast.Definition(1)
                    },
                    200
            );
        }

        public static byte[] createSerializedDefaultCharacterDefinition() {
            return createDefaultCharacterDefinition().serializeToByteArray();
        }

        private static final class HealthBar {
            public static final String PATH_FULL = "images/health_bar/full.png";
            public static final String PATH_EMPTY = "images/health_bar/empty.png";

            public static final float WIDTH = getWorldWidth() * 0.06f;
            public static final float HEIGHT = getWorldHeight() * 0.013f;
        }
    }
}
