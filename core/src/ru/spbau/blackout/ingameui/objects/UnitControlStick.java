package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.network.UIServer;

public class UnitControlStick extends AbstractStick {

    /** the controlled unit */
    private /*final*/ GameUnit unit;

    public UnitControlStick(Stage stage, UIServer server) {
        super(stage, server);
    }

    @Override
    public void doneLoading(GameContext context) {
        super.doneLoading(context);
        this.unit = context.getMainCharacter();
    }

    @Override
    protected void onTouchMove() {
        super.onTouchMove();
        server.sendSelfVelocity(unit, touchPos);
    }
}
