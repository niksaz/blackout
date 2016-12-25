package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.graphics.Camera;

import ru.spbau.blackout.ingameui.objects.CameraControlStick;
import ru.spbau.blackout.ingameui.objects.ExitButton;
import ru.spbau.blackout.network.UIServer;

public class ViewerUI extends IngameUI {

    /**
     * Substitutes the previous ui by the new one. Disposes the previous ui.
     */
    public ViewerUI(IngameUI previous, UIServer server, Camera camera) {
        super(previous.getExtraActors());
        previous.dispose();

        addUiObject(new CameraControlStick(server, camera));
        addUiObject(new ExitButton());
    }
}
