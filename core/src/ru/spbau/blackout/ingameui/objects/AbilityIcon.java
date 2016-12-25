package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.jetbrains.annotations.Nullable;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.IngameUIObject;
import ru.spbau.blackout.network.UIServer;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.progressbar.VerticalProgressBar;
import ru.spbau.blackout.utils.Textures;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.utils.Utils.EMPTY_TEXTURE_PATH;
import static ru.spbau.blackout.utils.Utils.floatEq;

public final class AbilityIcon extends IngameUIObject {
    // fields marked as /*final*/ must be assigned only once inside initializeGameWorld method.

    public static final String FULL_TEXTURE_PATH = "images/ability_cell/full.png";
    public static final String CHARGED_TEXTURE_PATH = "images/ability_cell/charged.png";
    public static final float CELL_SIZE = getWorldHeight() * 0.17f;
    public static final float ICON_SIZE = 0.8f * CELL_SIZE;


    @Nullable private /*final*/ Ability ability;
    private boolean isPressed = false;
    private SimpleProgressBar chargingBar = new VerticalProgressBar(EMPTY_TEXTURE_PATH, FULL_TEXTURE_PATH);
    private final UIServer server;  // TODO: use it
    private final int abilityNum;
    private final Vector2 startPosition;


    // I have to getOriginal unitDef here in order to getOriginal its abilityIcons
    public AbilityIcon(UIServer server, int abilityNum, Vector2 startPosition) {
        this.server = server;
        this.abilityNum = abilityNum;
        this.startPosition = startPosition;
    }


    @Override
    public void load(GameContext context) {
        chargingBar.load(context.getAssets());
        Textures.loadMipMapAA(CHARGED_TEXTURE_PATH, context.getAssets());
    }

    @Override
    public void doneLoading(GameContext context, Stage stage) {
        if (abilityNum >= context.getMainCharacter().getAbilities().size()) {
            return;
        }

        ability = context.getMainCharacter().getAbility(abilityNum);
        assert ability != null;

        // Charged cell image
        Image ready = new Image(context.getAssets().get(CHARGED_TEXTURE_PATH, Texture.class));
        ready.setSize(CELL_SIZE, CELL_SIZE);
        ready.setPosition(startPosition.x, startPosition.y);
        ready.setZIndex(0);
        stage.addActor(ready);

        // icon initialization
        Image icon = new Image(context.getAssets().get(ability.getDef().getIconPath(), Texture.class));
        icon.setSize(ICON_SIZE, ICON_SIZE);
        float iconOffset = (CELL_SIZE - ICON_SIZE) / 2f;
        icon.setPosition(startPosition.x + iconOffset, startPosition.y + iconOffset);

        icon.setZIndex(1);
        stage.addActor(icon);
        icon.addListener(this.new Listener());

        // chargingBar initialization
        chargingBar.doneLoading(context.getAssets());
        chargingBar.setSize(CELL_SIZE, CELL_SIZE);
        chargingBar.setPosition(startPosition.x, startPosition.y);
        chargingBar.setZIndex(2);
        endCharging();
        stage.addActor(chargingBar);
    }

    @Override
    public void update(float delta) {
        if (ability == null) {
            return;
        }

        if (isPressed) {
            ability.inCast(server, delta);
        }

        float chargeTime = ability.getChargeTime();
        if (floatEq(chargeTime, 0)) {
            if (isCharging()) {
                // stop charging
                endCharging();
            }
        } else {
            if (isCharging()) {
                // updatePhysics charging
                chargingBar.setValue(chargeTime / ability.getDef().getMaxChargeTime());
            } else {
                // start charging
                chargingBar.setValueInstant(chargeTime / ability.getDef().getMaxChargeTime());
                chargingBar.setVisible(true);
            }
        }
    }

    public boolean isCharging() {
        return chargingBar.isVisible();
    }

    private void endCharging() {
        chargingBar.setVisible(false);
        // FIXME: I can't explain it, but there is a strange unpleasant effect without this line.
        chargingBar.setValueInstant(1);
    }

    @Override
    public void dispose() {
        // TODO
    }


    private class Listener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            assert ability != null;
            ability.onCastStart(server);
            isPressed = true;

            // It means that I want it to receive all touchDragged and touchUp events,
            // even those not over this actor, until touchUp is received.
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            assert ability != null;
            isPressed = false;
            ability.onCastEnd(server);
        }
    }
}
