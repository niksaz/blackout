package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.graphics.Camera;

import ru.spbau.blackout.ingameui.objects.CameraControlStick;
import ru.spbau.blackout.ingameui.objects.ExitButton;
import ru.spbau.blackout.network.UIServer;

public class ObserverUI extends IngameUI {

    /**
     * Substitutes the previous ui by the new one. Disposes the previous ui.
     */
    public ObserverUI(IngameUI previous, UIServer server, Camera camera) {
        super(previous);

        addUiObject(new CameraControlStick(getStage(), server, camera));
        addUiObject(new ExitButton(getStage()));
    }
}
