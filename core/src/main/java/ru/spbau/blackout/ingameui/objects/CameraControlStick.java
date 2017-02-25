package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.network.UIServer;

public class CameraControlStick extends AbstractStick {

    private static final float SPEED = 0.3f;
    private final Camera camera;

    public CameraControlStick(Stage stage, UIServer server, Camera camera) {
        super(stage, server);
        this.camera = camera;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        camera.position.set(
                camera.position.x + touchPos.x * SPEED,
                camera.position.y + touchPos.y * SPEED,
                camera.position.z
        );
    }
}
