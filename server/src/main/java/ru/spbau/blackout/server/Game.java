package ru.spbau.blackout.server;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.rooms.TestingSessionSettings;

class Game extends Thread {

    private static AtomicInteger gamesCreated = new AtomicInteger();

    private final int gameId;
    private final RoomServer server;
    private final List<ClientThread> clients;
    private AtomicReference<GameState> gameState = new AtomicReference<>(GameState.READY_TO_START);

    Game(RoomServer server, List<ClientThread> clients) {
        gameId = gamesCreated.getAndAdd(1);
        this.server = server;
        this.clients = clients;
    }

    public void run() {
        server.log("New game with id #" + gameId + " has just started!");
        for (ClientThread thread : clients) {
            thread.setGame(this);
        }
        for (ClientThread thread : clients) {
            GameState currentClientGameState = thread.getClientGameState();
            while (currentClientGameState == GameState.WAITING) {
                try {
                    sleep(Network.WAITING_TO_READY_CYCLE_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentClientGameState = thread.getClientGameState();
            }
            if (currentClientGameState == GameState.FINISHED) {
                gameState.set(GameState.FINISHED);
            }
        }

        if (gameState.get() == GameState.FINISHED) {
            return;
        }

        // !!!!!!

        TestingSessionSettings room = new TestingSessionSettings();
        room.map =  "maps/duel/duel.g3db";

        Shape heroShape = new CircleShape();
        heroShape.setRadius(0.7f);
        Hero.Definition hero = new Hero.Definition("models/wizard/wizard.g3db", heroShape, 0, 0);

        Shape heroShape2 = new CircleShape();
        heroShape.setRadius(0.7f);
        Hero.Definition hero2 = new Hero.Definition("models/wizard/wizard.g3db", heroShape, 50, 50);

        room.objectDefs.add(hero);

        room.objectDefs.add(hero2);

        Shape stoneShape = new CircleShape();
        heroShape.setRadius(1.5f);
        GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db", stoneShape, 0, -20
        );
        room.objectDefs.add(stone);

        GameWorld gameWorld = new GameWorld();
        for (GameObject.Definition def : room.getObjectDefs()) {
            def.makeInstance(null, gameWorld);
        }

        room.character = hero;
        // serialize and send

        room.character = hero2;
        // serialize and send



        // !!!!!!!

        gameState.set(GameState.IN_PROCESS);

        do {
            for (ClientThread thread : clients) {
                GameState currentClientGameState = thread.getClientGameState();
                if (currentClientGameState == GameState.FINISHED) {
                    server.log(thread.getClientName() +
                               " disconnected. Game with id #" + gameId + " will be finished.");
                    gameState.set(GameState.FINISHED);
                    break;
                }
            }
        } while (gameState.get() != GameState.FINISHED);
    }

    GameState getGameState() {
        return gameState.get();
    }
}
