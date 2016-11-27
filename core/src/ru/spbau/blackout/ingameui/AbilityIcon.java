package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sun.javafx.print.Units;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.network.AbstractServer;

public class AbilityIcon extends IngameUIObject {
    // fields marked as /*final*/ must be assigned only once inside doneLoading method.

    private final AbilityIconSettings settings;
    private /*final*/ Image icon;
    private /*final*/ GameUnit unit;
    private /*final*/ Ability ability;
    private boolean isPressed = false;


    // I have to get unitDef here in order to get its abilityIcons
    public AbilityIcon(AbstractServer server, AbilityIconSettings settings) {
        super(server);
        this.settings = settings;
    }


    @Override
    public void load(AssetManager assets) { /*nothing*/ }

    @Override
    public void doneLoading(AssetManager assets, Stage stage, Hero hero) {
        this.ability = hero.getAbility(this.settings.getAbilityNum());

        // icon initialization
        this.icon = new Image(assets.get(this.getAbility().iconPath(), Texture.class));
        this.icon.setSize(this.settings.getSizeX(), this.settings.getSizeY());
        this.icon.setPosition(settings.getStartX(), settings.getStartY());
        stage.addActor(this.icon);
        this.icon.addListener(this.new Listener());
    }

    @Override
    public void update(float deltaTime) {
//        if (this.isPressed) {
//            this.getAbilityInst().
//        }
        // TODO
    }


    public Ability getAbility() { return this.ability; }


    private class Listener extends InputListener {

    }
}
