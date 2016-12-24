package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.androidfeatures.PlayServicesListener;
import ru.spbau.blackout.database.ChangeablePlayerProfile;

/**
 * When the app is signing-in a player and loading the profile from the server LoadScreen is showed. Also this class
 * contains sign-in failure handling.
 */
public class LoadScreen extends StageScreen implements PlayServicesListener {

    private static final float LABEL_BOTTOM_PADDING = 25.0f;
    private static final float DIALOG_PADDING = 15.0f;

    private static final String STARTED_LOGGING_IN = "Signing in...";
    private static final String STARTED_LOADING = "Loading your game info...";
    private static final String TRY_AGAIN = "Try again";
    private static final String UNSUCCESSFUL_MES = "Unsuccessful sign in attempt";
    private static final String UNSUCCESSFUL_LOADING = "The server is unavailable.";

    private Table middleTable;

    public LoadScreen() {
        super();

        middleTable = new Table();
        middleTable.setFillParent(true);
        stage.addActor(middleTable);

        BlackoutGame.get().playServicesInCore().addListener(this);
    }

    @Override
    public void show() {
        super.show();
        initiateSignIn();
    }

    public void initiateSignIn() {
        middleTable.clear();

        addLabel(STARTED_LOGGING_IN);
        BlackoutGame.get().playServicesInCore().getPlayServices().signIn();
    }

    @Override
    public void onSignInSucceeded() {
        Gdx.app.postRunnable(() -> addLabel(STARTED_LOADING));
        ChangeablePlayerProfile.loadPlayerEntity(
                () -> Gdx.app.postRunnable(() -> BlackoutGame.get().screenManager().setScreen(new MenuScreen())),
                () -> Gdx.app.postRunnable(() -> showErrorDialog(UNSUCCESSFUL_LOADING)));
    }

    @Override
    public void onSignInFailed(String message) {
        showErrorDialog(message);
    }

    @Override
    public void render(float delta) {
        final Color color = BlackoutGame.get().assets().getBackgroundColor();
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    public void showErrorDialog(String text) {
        middleTable.clear();
        new Dialog(UNSUCCESSFUL_MES, BlackoutGame.get().assets().getDefaultSkin()) {
            {
                setMovable(false);
                padTop(getTitleLabel().getHeight());
                setWidth(getTitleLabel().getWidth());
                getTitleLabel().setAlignment(Align.center);
                padLeft(DIALOG_PADDING);
                padRight(DIALOG_PADDING);
                getContentTable().add(text).padTop(DIALOG_PADDING).padBottom(DIALOG_PADDING);
                button(TRY_AGAIN).padBottom(DIALOG_PADDING);
            }

            @Override
            protected void result(Object object) {
                super.result(object);
                this.remove();
                initiateSignIn();
            }
        }.show(stage);
    }

    private Label addLabel(CharSequence text) {
        final Label label = new Label(text, BlackoutGame.get().assets().getDefaultSkin());
        middleTable.add(label).pad(LABEL_BOTTOM_PADDING).row();
        return label;
    }
}
