package ru.spbau.blackout.database;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.androidfeatures.PlayServices;
import ru.spbau.blackout.network.Network;

/**
 * Extended PlayerProfile which actually asks server if a query can be done and only then performs it.
 */
public class ChangeablePlayerProfile extends PlayerProfile {

    private static final int HTTP_CONNECT_TIMEOUT_MS = 2000;
    private static final int HTTP_READ_TIMEOUT_MS = 2000;
    private static final int LOAD_REQUEST_MAX_ATTEMPTS = 3;

    public ChangeablePlayerProfile(PlayerProfile playerProfile) {
        super(playerProfile);
    }

    public void upgradeHealth() {
        makeUpgradeRequest(
                outputStream -> {
                    outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                    outputStream.writeUTF(Database.HEALTH_UPGRADE);
                }
        );
    }

    public void upgradeAbility(int currentAbilityIndex) {
        makeUpgradeRequest(
                outputStream -> {
                    outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                    outputStream.writeUTF(Database.ABILITY_UPGRADE);
                    outputStream.writeInt(currentAbilityIndex);
                }
        );
    }

    public void synchronizeGameSettings() {
        new Thread(() -> {
            try {
                final HttpURLConnection connection =
                        openHttpURLConnectionForServerCommand(Database.SETTINGS_SYNCHRONIZE_COMMAND);

                try (ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream())
                ) {
                    outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                    outputStream.writeObject(getSerializedSettings());
                }

                connection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void makeUpgradeRequest(RequestWriter requestWriter) {
        startUpgradeRequest(
                requestWriter,
                () -> {
                    final PlayServices playServices = BlackoutGame.get().playServicesInCore().getPlayServices();
                    playServices.incrementAchievement(playServices.getFirstUpgradesAchievementID(), 1);
                }
        );
    }

    private void startUpgradeRequest(RequestWriter requestWriter, Runnable ifSuccessful) {
        new Thread(() -> {
            try {
                final HttpURLConnection connection =
                        openHttpURLConnectionForServerCommand(Database.UPGRADE_COMMAND);

                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
                ) {
                    requestWriter.writeRequest(outputStream);
                }

                final int responseCode = connection.getResponseCode();
                System.out.println(
                        "Updating. " + connection.getRequestMethod() + " request to URL : " + connection.getURL());
                System.out.println("Response Code : " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    loadPlayerEntity(null, null);
                    if (ifSuccessful != null) {
                        ifSuccessful.run();
                    }
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
                    final HttpURLConnection connection =
                            openHttpURLConnectionForServerCommand(Database.LOAD_COMMAND);

                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
                    ) {
                        outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
                    }

                    final int responseCode = connection.getResponseCode();
                    final int responseLength = connection.getContentLength();
                    System.out.println(
                            "Loading. " + connection.getRequestMethod() + " request to URL : " + connection.getURL());
                    System.out.println("Response Code : " + responseCode);
                    System.out.println("Response Length : " + responseLength);

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        continue;
                    }
                    final ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

                    final PlayerProfile playerProfile = (PlayerProfile) in.readObject();
                    BlackoutGame.get().setPlayerEntity(new ChangeablePlayerProfile(playerProfile));
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

    private static HttpURLConnection openHttpURLConnectionForServerCommand(String command) throws IOException {
        final String urlString =
                "http://" +
                Network.SERVER_IP_ADDRESS +
                ':' +
                Network.SERVER_HTTP_PORT_NUMBER +
                command;

        final URL url = new URL(urlString);

        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(HTTP_READ_TIMEOUT_MS);

        return connection;
    }

    @FunctionalInterface
    private interface RequestWriter {
        void writeRequest(DataOutputStream outputStream) throws IOException;
    }
}
