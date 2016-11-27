package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.network.AbstractServer;

public class AbilityIcon extends DragListener {
    private final AbilityIconSettings settings;
    private Hero hero;
    private Image icon;

    public AbilityIcon(AbstractServer server, AbilityIconSettings settings) {
        this.settings = settings;
        // TODO: settings
    }

    public void load(AssetManager assets) {
        assets.load(this.getAbility().iconPath(), Texture.class);
    }

    public void doneLoading(AssetManager assets, Stage stage, Hero hero) {
        this.hero = hero;

        // icon initialization
        this.icon = new Image(assets.get(this.getAbility().iconPath(), Texture.class));
        this.icon.setSize(this.settings.getSizeX(), this.settings.getSizeY());
        this.icon.setPosition(settings.getStartX(), settings.getStartY());
        stage.addActor(this.icon);
        this.icon.addListener(this.getAbility().getIconInputListener());
    }

    public Ability getAbility() {
        return this.settings.getAbility();
    }
}
