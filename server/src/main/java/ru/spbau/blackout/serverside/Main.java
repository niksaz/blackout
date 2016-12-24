package ru.spbau.blackout.serverside;

import com.badlogic.gdx.physics.box2d.Box2D;

import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.serverside.servers.HttpRequestServer;
import ru.spbau.blackout.serverside.servers.RoomServer;

/**
 * Class for starting servers for different purposes, i.e. multiplayer, loading game info.
 */
public class Main {

    public static void main(String[] args) {
        Box2D.init();
        new HttpRequestServer(Network.SERVER_HTTP_PORT_NUMBER, System.out, "HTTP").start();
        for (Network.RoomServerDescription description : Network.ROOM_SERVERS) {
            final RoomServer roomServer =
                    new RoomServer(
                        description.getPort(),
                        description.getPlayersToStart(),
                        System.out,
                        "ROOM " + description.getPort());
            roomServer.start();
        }
    }
}
