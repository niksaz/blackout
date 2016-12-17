package ru.spbau.blackout.network;

public class Network {

    public static final int STATE_UPDATE_CYCLE_MS = 250;
    public static final int SOCKET_IO_TIMEOUT_MS = 5000;

    public static final long TIME_SHOULD_BE_SPENT_IN_ITERATION = 17;

    public static final String SERVER_IP_ADDRESS = "192.168.1.34";
    public static final int SERVER_TCP_PORT_NUMBER = 48800;
    public static final int SERVER_HTTP_PORT_NUMBER = 48801;

    public static final int DATAGRAM_WORLD_PACKET_SIZE = 1024;
    public static final int DATAGRAM_VELOCITY_PACKET_SIZE = 128;

    private Network() {}
}
