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
import ru.spbau.blackout.database.PlayerEntityAtClient;

public class LoadScreen extends StageScreen implements PlayServicesListener {

    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    private static final float LABEL_BOTTOM_PADDING = 25.0f;
    private static final float DIALOG_PADDING = 15.0f;

    private static final String STARTED_LOGGING_IN = "Signing in...";
    private static final String STARTED_LOADING = "Loading your game info...";
    private static final String TRY_AGAIN = "Try again";
    private static final String UNSUCCESSFUL_MES = "Unsuccessful sign in attempt";

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
        PlayerEntityAtClient.loadPlayerEntity(this);
    }

    @Override
    public void onSignInFailed(String message) {
        showErrorDialog(message);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                BACKGROUND_COLOR.r, BACKGROUND_COLOR.g,
                BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    public void showErrorDialog(String text) {
        middleTable.clear();
        new Dialog(UNSUCCESSFUL_MES, BlackoutGame.get().assets().getDefaultSkin()) {
            {
                setMovable(false);
                padTop(getTitleLabel().getHeight());
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
        final Label.LabelStyle style = new Label.LabelStyle(BlackoutGame.get().assets().getFont(), LABEL_COLOR);
        final Label label = new Label(text , style);

        middleTable.add(label).pad(LABEL_BOTTOM_PADDING).row();

        return label;
    }
}
