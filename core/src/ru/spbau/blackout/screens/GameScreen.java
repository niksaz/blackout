package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.rooms.GameRoom;

public class GameScreen extends BlackoutScreen {
    private TiledMap map;
    private GameRoom room;

    private PerspectiveCamera camera;
    private GameUnit[] units;
    private Hero hero;

    // just for test
    private Model model;
    private ModelBatch modelBatch;
    public Environment environment;

    private AssetManager assets;
    private boolean loading;

    public GameScreen(BlackoutGame blackoutGame, GameRoom room) {
        super(blackoutGame);
        this.room = room;
    }

    @Override
    public void show() {
        modelBatch = new ModelBatch();
        map = new TmxMapLoader().load(room.mapPath);

        camera = new PerspectiveCamera();
        camera.fieldOfView = 67;
        camera.position.set(0f, 1000f, 1000f);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 30000f;

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        assets = new AssetManager();
        assets.load("models/ship/mage.g3dj", Model.class);
        loading = true;
    }

    private void doneLoading() {
        model = assets.get("models/ship/mage.g3dj", Model.class);
        if (model == null) {
            throw new IllegalArgumentException();
        }
        hero = new Hero(model, 0, 0);
        loading = false;
    }

    @Override
    public void render(float delta) {
        if (loading) {
            if (assets.update()) {
                doneLoading();
            }
        } else {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            modelBatch.begin(camera);
            modelBatch.render(hero.forRender(delta), environment);
            modelBatch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
        assets.dispose();
    }
}
