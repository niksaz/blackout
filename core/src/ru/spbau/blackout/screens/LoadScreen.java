package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerEntity;
import ru.spbau.blackout.database.PlayerEntityAtClient;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.play.services.CorePlayServicesListener;

public class LoadScreen extends StageScreen implements CorePlayServicesListener {

    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    private static final float LABEL_BOTTOM_PADDING = 25.0f;

    private static final String STARTED_LOG_IN = "Logging in...";  // FIXME: localization (move to resources)
    private static final String STARTED_LOADING = "Loading your game info...";  // FIXME: localization

    private Table middleTable;

    public LoadScreen() {
        super();
    }

    @Override
    public void show() {
        super.show();

        BlackoutGame.get().playServicesInCore().addListener(this);

        middleTable = new Table();
        middleTable.setFillParent(true);
        stage.addActor(middleTable);

        addLabel(STARTED_LOG_IN);
        BlackoutGame.get().playServicesInCore().getPlayServices().signIn();
    }

    @Override
    public void onSignInSucceeded() {
        Gdx.app.postRunnable(() -> addLabel(STARTED_LOADING));

        new Thread(() -> {
            try {
                final String url = "http://" +
                        Network.SERVER_IP_ADDRESS +
                        ':' +
                        Network.SERVER_HTTP_PORT_NUMBER +
                        Database.LOAD_COMMAND;


                final URL urlObject = new URL(url);
                final HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                try (
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
                ) {
                    outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                }

                final int responseCode = connection.getResponseCode();
                final int responseLength = connection.getContentLength();
                System.out.println("Loading. " + connection.getRequestMethod() + " request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                final byte[] response = new byte[responseLength];
                final int result = connection.getInputStream().read(response);
                System.out.println("GOT " + responseLength + " " + Arrays.toString(response));

                final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(response));
                final PlayerEntity playerEntity = (PlayerEntity) in.readObject();
                BlackoutGame.get().setPlayerEntity(new PlayerEntityAtClient(playerEntity));
                Gdx.app.postRunnable(() ->
                        BlackoutGame.get().screenManager().setScreen(new MenuScreen()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void finishedLoadingSnapshot() {
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
        final Label.LabelStyle style = new Label.LabelStyle(BlackoutGame.get().assets().getFont(), LABEL_COLOR);
        final Label label = new Label(text , style);

        middleTable.add(label).pad(LABEL_BOTTOM_PADDING).row();

        return label;
    }

}
