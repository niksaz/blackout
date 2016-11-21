package ru.spbau.blackout.server;

import com.badlogic.gdx.physics.box2d.Box2D;

/**
 * Class for starting servers.
 */
public class Main {

    private static final int PORT = 48800;

    public static void main(String[] args) {
        Box2D.init();

        new RoomServer(PORT).run();
    }
}
