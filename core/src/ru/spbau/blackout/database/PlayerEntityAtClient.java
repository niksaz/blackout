package ru.spbau.blackout.database;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.network.Network;

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
}
