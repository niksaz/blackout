package ru.spbau.blackout.network;

import java.util.ArrayList;
import java.util.List;

public class Network {

    public static final int STATE_UPDATE_CYCLE_MS = 250;
    public static final int SOCKET_IO_TIMEOUT_MS = 5000;

    public static final long TIME_SHOULD_BE_SPENT_IN_ITERATION = 30;

    public static final String SERVER_IP_ADDRESS = "192.168.1.34";
    public static final int SERVER_HTTP_PORT_NUMBER = 48800;
    public static final List<RoomServerDescription> ROOM_SERVERS = new ArrayList<RoomServerDescription>() {
        {
            add(new RoomServerDescription(48801, 2));
            add(new RoomServerDescription(48802, 3));
            add(new RoomServerDescription(48803, 4));
        }
    };
    public static final int DATAGRAM_WORLD_PACKET_SIZE = 1024;
    public static final int DATAGRAM_VELOCITY_PACKET_SIZE = 128;

    private Network() {}

    public static class RoomServerDescription {

        int port;
        int playersToStart;

        RoomServerDescription(int port, int playersToStart) {
            this.port = port;
            this.playersToStart = playersToStart;
        }

        public int getPort() {
            return port;
        }

        public int getPlayersToStart() {
            return playersToStart;
        }
    }
}
