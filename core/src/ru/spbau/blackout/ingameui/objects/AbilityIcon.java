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
import ru.spbau.blackout.network.AbstractServer;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.progressbar.VerticalProgressBar;
import ru.spbau.blackout.utils.Textures;

import static ru.spbau.blackout.utils.Utils.EMPTY_TEXTURE_PATH;
import static ru.spbau.blackout.utils.Utils.floatEq;

public class AbilityIcon extends IngameUIObject {
    // fields marked as /*final*/ must be assigned only once inside initialize method.

    public static final String FULL_TEXTURE_PATH = "images/ability_cell/full.png";
    public static final String CHARGED_TEXTURE_PATH = "images/ability_cell/charged.png";


    private final AbilityIconSettings settings;
    private /*final*/ Ability ability;
    private boolean isPressed = false;
    private SimpleProgressBar chargingBar = new VerticalProgressBar(EMPTY_TEXTURE_PATH, FULL_TEXTURE_PATH);


    // I have to get unitDef here in order to get its abilityIcons
    public AbilityIcon(AbstractServer server, AbilityIconSettings settings) {
        super(server);
        this.settings = settings;
    }


    @Override
    public void load(AssetManager assets) {
        this.chargingBar.load(assets);
        Textures.loadMipMapAA(CHARGED_TEXTURE_PATH, assets);
    }

    @Override
    public void doneLoading(AssetManager assets, Stage stage, Character character) {
        this.ability = character.getAbility(this.settings.getAbilityNum());

        // Charged cell image
        Image ready = new Image(assets.get(CHARGED_TEXTURE_PATH, Texture.class));
//        Image ready = new Image(Utils.addAntiAliasing(assets.get(CHARGED_TEXTURE_PATH, Texture.class)));
        ready.setSize(this.settings.getSize().x, this.settings.getSize().y);
        ready.setPosition(settings.getStart().x, settings.getStart().y);
        ready.setZIndex(0);
        stage.addActor(ready);

        // icon initialization
        Image icon = new Image(assets.get(this.ability.iconPath(), Texture.class));
        icon.setSize(this.settings.getSize().x, this.settings.getSize().y);
        icon.setPosition(settings.getStart().x, settings.getStart().y);
        icon.setZIndex(1);
        stage.addActor(icon);
        icon.addListener(this.new Listener());

        // chargingBar initialization
        this.chargingBar.doneLoading(assets);
        this.chargingBar.setSize(this.settings.getSize().x, this.settings.getSize().y);
        this.chargingBar.setPosition(settings.getStart().x, settings.getStart().y);
        this.chargingBar.setZIndex(2);
        this.endCharging();
        stage.addActor(this.chargingBar);
    }

    @Override
    public void update(float deltaTime) {
        if (this.isPressed) {
            this.ability.inCast(deltaTime);
        }

        float chargeTime = this.ability.getChargeTime();
        if (floatEq(chargeTime, 0)) {
            if (this.isCharging()) {
                // stop charging
                this.endCharging();
            }
        } else {
            if (this.isCharging()) {
                // update charging
                this.chargingBar.setValue(chargeTime / this.ability.getMaxChargeTime());
                this.ability.charge(deltaTime);
            } else {
                // start charging
                this.chargingBar.setValueInstant(chargeTime / this.ability.getMaxChargeTime());
                this.chargingBar.setVisible(true);
            }
        }
    }


    public Ability getAbility() { return this.ability; }


    public boolean isCharging() {
        return this.chargingBar.isVisible();
    }

    private void endCharging() {
        this.chargingBar.setVisible(false);
        // FIXME: I can't explain it, but there is a strange unpleasant effect without this line.
        this.chargingBar.setValueInstant(1);
    }


    private class Listener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            getAbility().onCastStart();
            isPressed = true;

            // It means that I want it to receive all touchDragged and touchUp events,
            // even those not over this actor, until touchUp is received.
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            isPressed = false;
            getAbility().onCastEnd();
        }
    }
}
