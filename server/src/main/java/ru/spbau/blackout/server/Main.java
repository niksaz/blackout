package ru.spbau.blackout.server;

import com.badlogic.gdx.physics.box2d.Box2D;

import ru.spbau.blackout.network.Network;

/**
 * Class for starting servers.
 */
public class Main {

    public static void main(String[] args) {
        Box2D.init();

        new RoomServer(Network.SERVER_TCP_PORT_NUMBER).run();
    }
}
