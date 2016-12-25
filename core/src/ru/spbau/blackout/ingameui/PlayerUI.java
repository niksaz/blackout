package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.spbau.blackout.ingameui.objects.AbilityIcon;
import ru.spbau.blackout.ingameui.objects.ExitButton;
import ru.spbau.blackout.ingameui.objects.HealthBar;
import ru.spbau.blackout.ingameui.objects.AbstractStick;
import ru.spbau.blackout.ingameui.objects.UnitControlStick;
import ru.spbau.blackout.network.UIServer;


/**
 * Main class for in-game user interface.
 */
public class PlayerUI extends IngameUI {

    // FIXME: calculate positions with regard to aspect ratio
    private static final Vector2[] ABILITY_ICONS_POS = {
            new Vector2(1100, 450),
            new Vector2(1100, 300),
            new Vector2(1100, 150)
    };

    /**
     * Creates all UI elements and sets itself as input processor.
     */
    public PlayerUI(UIServer server, List<Actor> extraActors) {
        super(extraActors);

        addUiObject(new UnitControlStick(server));
        addUiObject(new HealthBar());
        addUiObject(new ExitButton());

        for (int i = 0; i < ABILITY_ICONS_POS.length; i++) {
            addUiObject(new AbilityIcon(server, i, ABILITY_ICONS_POS[i]));
        }
    }

    public PlayerUI(UIServer server) {
        this(server, new LinkedList<>());
    }
}
