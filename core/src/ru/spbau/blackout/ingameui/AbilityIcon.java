package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sun.javafx.print.Units;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.network.AbstractServer;

public class AbilityIcon extends InputListener {
    // fields marked as /*final*/ must be assigned only once inside doneLoading method.

    private final AbilityIconSettings settings;
    private final AbstractServer server;
    private /*final*/ Image icon;
    private /*final*/ GameUnit unit;
    private /*final*/ Ability ability;
    private boolean isPressed = false;

    // I have to get unitDef here in order to get its abilityIcons
    public AbilityIcon(AbstractServer server, AbilityIconSettings settings) {
        this.server = server;
        this.settings = settings;
    }

    // TODO: make abstract class IngameUIElement
    public void load(AssetManager assets) {}

    public void update(float deltaTime) {
//        if (this.isPressed) {
//            this.getAbilityInst().
//        }
        // TODO
    }

    public void doneLoading(AssetManager assets, Stage stage, GameUnit unit) {
        this.ability = unit.getAbility(this.settings.getAbilityNum());

        // icon initialization
        this.icon = new Image(assets.get(this.getAbility().iconPath(), Texture.class));
        this.icon.setSize(this.settings.getSizeX(), this.settings.getSizeY());
        this.icon.setPosition(settings.getStartX(), settings.getStartY());
        stage.addActor(this.icon);
        this.icon.addListener(this);
    }

    public Ability getAbility() {
        return this.ability;
    }
}
