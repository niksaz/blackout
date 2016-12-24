package ru.spbau.blackout.database;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.network.Network;

/**
 * Extended PlayerEntity which actually asks server if a query can be done and only then performs it.
 */
public class ChangeablePlayerEntity extends PlayerEntity {

    private static final int HTTP_CONNECT_TIMEOUT_MS = 2000;
    private static final int HTTP_READ_TIMEOUT_MS = 2000;
    private static final int LOAD_REQUEST_MAX_ATTEMPTS = 3;

    public ChangeablePlayerEntity(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    public void changeGold(int delta) {
        startUpgradeRequest(outputStream -> {
            outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
            outputStream.writeUTF(Database.GOLD_UPGRADE);
            outputStream.writeInt(delta);
        });
    }

    public void upgradeHealth() {
        startUpgradeRequest(outputStream -> {
            outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
            outputStream.writeUTF(Database.HEALTH_UPGRADE);
        });
    }

    private void startUpgradeRequest(RequestWriter requestWriter) {
        new Thread(() -> {
            try {
                final String url = "http://" +
                        Network.SERVER_IP_ADDRESS +
                        ':' +
                        Network.SERVER_HTTP_PORT_NUMBER +
                        Database.UPGRADE_COMMAND;

                final URL urlObject = new URL(url);
                final HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(HTTP_READ_TIMEOUT_MS);

                try (
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
                ) {
                    requestWriter.writeRequest(outputStream);
                }

                final int responseCode = connection.getResponseCode();
                System.out.println("Updating. " + connection.getRequestMethod() + " request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    loadPlayerEntity(null, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void loadPlayerEntity(Runnable runIfSuccessful, Runnable runIfUnsuccessful) {
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

                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
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
                    BlackoutGame.get().setPlayerEntity(new ChangeablePlayerEntity(playerEntity));
                    if (runIfSuccessful != null) {
                        runIfSuccessful.run();
                    }
                    loadSuccessfully = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!loadSuccessfully && runIfUnsuccessful != null) {
                runIfUnsuccessful.run();
            }
        }).start();
    }

    @FunctionalInterface
    private interface RequestWriter {
        void writeRequest(DataOutputStream outputStream) throws IOException;
    }
}
