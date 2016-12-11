package ru.spbau.blackout.database;

import com.badlogic.gdx.Gdx;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.screens.MenuScreen;

/**
 * Extended PlayerEntity which actually asks server if a query can be done and only then performs it.
 */
public class PlayerEntityAtClient extends PlayerEntity {

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

                try (
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
                ) {
                    outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                }

                final int responseCode = connection.getResponseCode();
                System.out.println("Updating. " + connection.getRequestMethod() + " request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    super.changeGold(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void loadPlayerEntity() {
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
}
