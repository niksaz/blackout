package ru.spbau.blackout.database;

import com.badlogic.gdx.Gdx;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.screens.LoadScreen;
import ru.spbau.blackout.screens.MenuScreen;

/**
 * Extended PlayerEntity which actually asks server if a query can be done and only then performs it.
 */
public class PlayerEntityAtClient extends PlayerEntity {

    private static final int HTTP_CONNECT_TIMEOUT_MS = 2000;
    private static final int HTTP_READ_TIMEOUT_MS = 2000;
    private static final int LOAD_REQUEST_MAX_ATTEMPTS = 3;
    private static final String UNSUCCESSFUL_LOADING = "The server is unavailable.";

    public PlayerEntityAtClient(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    @Override
    public void changeGold(int delta) {
        new Thread(() -> {
            try {
                final String url = "http://" +
                        Network.SERVER_IP_ADDRESS +
                        ':' +
                        Network.SERVER_HTTP_PORT_NUMBER +
                        Database.UPDATE_COMMAND;

                final URL urlObject = new URL(url);
                final HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(HTTP_READ_TIMEOUT_MS);

                try (
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
                ) {
                    outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                    outputStream.writeInt(delta);
                }

                final int responseCode = connection.getResponseCode();
                System.out.println("Updating. " + connection.getRequestMethod() + " request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    super.changeGold(delta);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void loadPlayerEntity(LoadScreen loadScreen) {
        new Thread(() -> {
            boolean loadSuccessfully = false;
            for (int attempt = 0; attempt < LOAD_REQUEST_MAX_ATTEMPTS; attempt++) {
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

                    connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
                    connection.setReadTimeout(HTTP_READ_TIMEOUT_MS);

                    try (
                            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
                    ) {
                        outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                    }

                    final int responseCode = connection.getResponseCode();
                    final int responseLength = connection.getContentLength();
                    System.out.println("Loading. " + connection.getRequestMethod() + " request to URL : " + url);
                    System.out.println("Response Code : " + responseCode);
                    System.out.println("Response Length : " + responseLength);

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        continue;
                    }
                    final ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                    final PlayerEntity playerEntity = (PlayerEntity) in.readObject();
                    BlackoutGame.get().setPlayerEntity(new PlayerEntityAtClient(playerEntity));
                    Gdx.app.postRunnable(() ->
                            BlackoutGame.get().screenManager().setScreen(new MenuScreen()));
                    loadSuccessfully = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!loadSuccessfully) {
                Gdx.app.postRunnable(() -> loadScreen.showErrorDialog(UNSUCCESSFUL_LOADING));
            }
        }).start();
    }
}
