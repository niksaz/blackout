package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sun.javafx.print.Units;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.IngameUIObject;
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
    public void load(GameContext context) { /*nothing*/ }

    @Override
    public void doneLoading(GameContext context, Stage stage, Hero hero) {
        this.ability = hero.getAbility(this.settings.getAbilityNum());

        // icon initialization
        // TODO: assert context.assets().isPresent()
        this.icon = new Image(context.assets().get().get(this.getAbility().iconPath(), Texture.class));
        this.icon.setSize(this.settings.getSizeX(), this.settings.getSizeY());
        this.icon.setPosition(settings.getStartX(), settings.getStartY());
        stage.addActor(this.icon);
        this.icon.addListener(this.new Listener());
    }

    @Override
    public void update(float deltaTime) {
        if (this.isPressed) {
            this.getAbility().inCast(deltaTime);
        }
    }


    public Ability getAbility() { return this.ability; }


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
