package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.rooms.GameRoom;

public class GameScreen extends BlackoutScreen {
    private TiledMap map;
    private GameRoom room;

    private PerspectiveCamera camera;
    private Array<GameUnit> units;
    private Hero hero;

    // just for test
    private ModelBatch modelBatch;
    public Environment environment;

    private AssetManager assets;
    private HashSet<Model> models = new HashSet<Model>();
    private boolean loading;

    public GameScreen(BlackoutGame blackoutGame, GameRoom room) {
        super(blackoutGame);
        this.room = room;
    }

    @Override
    public void show() {
        modelBatch = new ModelBatch();
        map = new TmxMapLoader().load(room.getMap());

        camera = new PerspectiveCamera();
        camera.fieldOfView = 67;
        camera.position.set(0f, 10f, 10f);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 30000f;

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        units = room.getUnits();
        hero = room.getHero();

        assets = new AssetManager();
        for (GameUnit unit : units) {
            assets.load(unit.getModelPath(), Model.class);
        }
        loading = true;
    }

    private void doneLoading() {
        for (GameUnit unit : units) {
            Model model = assets.get(unit.getModelPath(), Model.class);
            unit.makeInstance(model);
            models.add(model);
        }
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
            for (GameUnit unit : units) {
                modelBatch.render(unit.forRender(delta), environment);
            }
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
        for (Model model : models) {
            model.dispose();
        }
        assets.dispose();
    }
}
