package ru.spbau.blackout.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.rooms.GameRoom;

public class GameScreen extends BlackoutScreen {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private GameRoom room;
    private OrthographicCamera camera;

    public GameScreen(BlackoutGame blackoutGame, GameRoom room) {
        super(blackoutGame);
        this.room = room;
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load(room.getMap());
        renderer = new OrthogonalTiledMapRenderer(map);
        camera = new OrthographicCamera();
//        renderer = new OrthogonalTiledMapRenderer(map, scale);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}
