package ru.spbau.blackout.serverside.servers.handlers;

import org.junit.Test;
import org.mongodb.morphia.query.Query;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;

import ru.spbau.blackout.database.ChangeablePlayerProfile;
import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.serverside.database.DatabaseAccessor;
import ru.spbau.blackout.serverside.servers.HttpRequestServer;

import static org.junit.Assert.assertEquals;

public class LoadRequestHandlerTest {

    private static final String LOCAL_IP_ADDRESS = "127.0.0.1";
    private static final String TEST_NAME = "TestCharacter";

    @Test
    public void testLoadRequest() throws Exception {
        new HttpRequestServer(Network.SERVER_HTTP_PORT_NUMBER, System.out, "HTTP").start();

        final HttpURLConnection connection =
                ChangeablePlayerProfile.openHttpURLConnectionForServerCommand(Database.LOAD_COMMAND, LOCAL_IP_ADDRESS);

        final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeUTF(TEST_NAME);
        final int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);
        final ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
        final PlayerProfile playerProfile = (PlayerProfile) in.readObject();
        assertEquals(TEST_NAME, playerProfile.getName());

        final Query<PlayerProfile> query = DatabaseAccessor.getInstance().queryProfile(TEST_NAME);
        assertEquals(1, query.asList().size());
    }
}
