package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import ru.spbau.blackout.abilities.InstantAbility;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.ShellObject;
import ru.spbau.blackout.shapescreators.CircleCreator;


public class FireballAbility extends InstantAbility {
    public static final String ICON_PATH = "abilities/fireball/icon.png";
    public static final String MODEL_PATH = "abilities/fireball/fireball.g3db";

    public FireballAbility(int level) {
        super(level);
    }

    @Override
    public void cast() {
        Gdx.app.log("Blackout", "fireball cast");
//         TODO: throw shell
//        ShellObject.Definition def = new ShellObject.Definition(MODEL_PATH, new CircleCreator(0.5f),
//                                            this.getUnit().getPosition().x, this.getUnit().getPosition().y + 2);
//
    }

    @Override
    public void load(AssetManager assets) {
        super.load(assets);
        // TODO: load fireball model
    }

    @Override
    public void doneLoading(AssetManager assets, GameUnit unit) {
        super.doneLoading(assets, unit);
    }

    @Override
    public String iconPath() { return ICON_PATH; }
}
