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
        new RoomServer(Network.SERVER_TCP_PORT_NUMBER, System.out, "ROOM").run();
    }
}
