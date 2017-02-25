package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.ingameui.objects.CameraControlStick;
import ru.spbau.blackout.network.UIServer;

public class ObserverUI extends IngameUI {
    public ObserverUI(Stage stage, UIServer server, Camera camera) {
        super(stage);

        addUiObject(new CameraControlStick(getStage(), server, camera));
    }
}
