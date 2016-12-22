package ru.spbau.blackout.entities;

import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.graphiceffects.GraphicEffect;
import ru.spbau.blackout.graphiceffects.HealthBarEffect;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.progressbar.HorizontalProgressBar;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.HasState;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;


public class Character extends GameUnit {
    public Character(GameUnit.Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
    }


    public static class Definition extends GameUnit.Definition {

        private static final long serialVersionUID = 1000000000L;
        private transient SimpleProgressBar healthBar;

        public Definition(String modelPath, Creator<Shape> shapeCreator, Ability[] abilities, float maxHealth) {
            super(modelPath, shapeCreator, abilities, maxHealth);
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            healthBar = new HorizontalProgressBar(HealthBar.PATH_EMPTY, HealthBar.PATH_FULL);
            healthBar.load(context.getAssets());
        }

        @Override
        public void doneLoading() {
            super.doneLoading();
            healthBar.doneLoading(context.getAssets());
            healthBar.setSize(HealthBar.WIDTH, HealthBar.HEIGHT);
            healthBar.toBack();
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

        private static final class HealthBar {
            public static final String PATH_FULL = "images/health_bar/full.png";
            public static final String PATH_EMPTY = "images/health_bar/empty.png";

            public static final float WIDTH = getWorldWidth() * 0.06f;
            public static final float HEIGHT = getWorldHeight() * 0.013f;
        }
    }
}
