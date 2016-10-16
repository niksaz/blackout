package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import ru.spbau.blackout.play.services.CorePlayServicesListener;
import ru.spbau.blackout.play.services.PlayServicesInCore;
import ru.spbau.blackout.utils.AssetLoader;
import ru.spbau.blackout.utils.ScreenManager;

public class LoadScreen extends StageScreen implements CorePlayServicesListener {

    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    private static final float LABEL_BOTTOM_PADDING = 25.0f;

    private static final String STARTED_LOG_IN = "Logging in...";
    private static final String STARTED_LOADING = "Loading your game info...";

    private Table middleTable;

    public LoadScreen() {
        super();
    }

    @Override
    public void show() {
        super.show();

        PlayServicesInCore.getInstance().addListener(this);

        middleTable = new Table();
        middleTable.setFillParent(true);
        stage.addActor(middleTable);

        addLabel(STARTED_LOG_IN);
        PlayServicesInCore.getInstance().getPlayServices().signIn();
    }

    @Override
    public void onSignInSucceeded() {
        Gdx.app.postRunnable(() -> addLabel(STARTED_LOADING));
        PlayServicesInCore.getInstance().getPlayServices().startLoadingSnapshot();
    }

    @Override
    public void finishedLoadingSnapshot() {
        PlayServicesInCore.getInstance().removeListener(this);
        middleTable.remove();
        middleTable = null;

        Gdx.app.postRunnable(() -> ScreenManager.getInstance().setScreen(new MenuScreen()));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                BACKGROUND_COLOR.r, BACKGROUND_COLOR.g,
                BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    private Label addLabel(CharSequence text) {
        final Label.LabelStyle style = new Label.LabelStyle(AssetLoader.getInstance().getFont(), LABEL_COLOR);
        final Label label = new Label(text , style);

        middleTable.add(label).pad(LABEL_BOTTOM_PADDING).row();

        return label;
    }

}
