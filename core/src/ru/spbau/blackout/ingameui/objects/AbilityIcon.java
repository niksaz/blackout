package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.IngameUIObject;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
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


    private final AbilityIconSettings settings;
    private /*final*/ Ability ability;
    private boolean isPressed = false;
    private SimpleProgressBar chargingBar = new VerticalProgressBar(EMPTY_TEXTURE_PATH, FULL_TEXTURE_PATH);
    private final UIServer server;  // TODO: use it


    // I have to getOriginal unitDef here in order to getOriginal its abilityIcons
    public AbilityIcon(UIServer server, AbilityIconSettings settings) {
        this.server = server;
        this.settings = settings;
    }


    @Override
    public void load(AssetManager assets) {
        chargingBar.load(assets);
        Textures.loadMipMapAA(CHARGED_TEXTURE_PATH, assets);
    }

    @Override
    public void doneLoading(AssetManager assets, Stage stage, Character character) {
        ability = character.getAbility(settings.getAbilityNum());

        // Charged cell image
        Image ready = new Image(assets.get(CHARGED_TEXTURE_PATH, Texture.class));
        ready.setSize(CELL_SIZE, CELL_SIZE);
        ready.setPosition(settings.getStart().x, settings.getStart().y);
        ready.setZIndex(0);
        stage.addActor(ready);

        // icon initialization
        Image icon = new Image(assets.get(ability.getDef().getIconPath(), Texture.class));
        icon.setSize(ICON_SIZE, ICON_SIZE);
        float iconOffset = (CELL_SIZE - ICON_SIZE) / 2f;
        icon.setPosition(settings.getStart().x + iconOffset, settings.getStart().y + iconOffset);

        icon.setZIndex(1);
        stage.addActor(icon);
        icon.addListener(this.new Listener());

        // chargingBar initialization
        chargingBar.doneLoading(assets);
        chargingBar.setSize(CELL_SIZE, CELL_SIZE);
        chargingBar.setPosition(settings.getStart().x, settings.getStart().y);
        chargingBar.setZIndex(2);
        endCharging();
        stage.addActor(chargingBar);
    }

    @Override
    public void update(float deltaTime) {
        if (isPressed) {
            ability.inCast(server, deltaTime);
        }

        float chargeTime = ability.getChargeTime();
        if (floatEq(chargeTime, 0)) {
            if (isCharging()) {
                // stop charging
                endCharging();
            }
        } else {
            if (isCharging()) {
                // updateState charging
                chargingBar.setValue(chargeTime / ability.getDef().getMaxChargeTime());
            } else {
                // start charging
                chargingBar.setValueInstant(chargeTime / ability.getDef().getMaxChargeTime());
                chargingBar.setVisible(true);
            }
        }
    }


    public Ability getAbility() { return ability; }


    public boolean isCharging() {
        return chargingBar.isVisible();
    }

    private void endCharging() {
        chargingBar.setVisible(false);
        // FIXME: I can't explain it, but there is a strange unpleasant effect without this line.
        chargingBar.setValueInstant(1);
    }


    private class Listener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            getAbility().onCastStart(server);
            isPressed = true;

            // It means that I want it to receive all touchDragged and touchUp events,
            // even those not over this actor, until touchUp is received.
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            isPressed = false;
            getAbility().onCastEnd(server);
        }
    }
}
