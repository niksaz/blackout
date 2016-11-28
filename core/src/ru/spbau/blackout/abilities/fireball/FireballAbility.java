package ru.spbau.blackout.abilities.fireball;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.InstantAbility;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.ShellObject;
import ru.spbau.blackout.shapescreators.CircleCreator;


public class FireballAbility extends InstantAbility {
    public static final String ICON_PATH = "abilities/fireball/icon.png";
    public static final String MODEL_PATH = "abilities/fireball/fireball.g3db";


    private final ShellObject.Definition shellDef;


    public FireballAbility(int level) {
        super(level);
        this.shellDef = new ShellObject.Definition(MODEL_PATH, new CircleCreator(1));
    }


    @Override
    public void cast() {
//        ShellObject shell = (ShellObject) shellDef.makeInstance(getUnit().getPosition().add(0, 2));
    }


    @Override
    public void load(GameContext context) {
        super.load(context);
        shellDef.load(context);
    }

    @Override
    public void doneLoading(GameContext context, GameUnit unit) {
        super.doneLoading(context, unit);
        shellDef.doneLoading(context);
    }

    @Override
    public String iconPath() { return ICON_PATH; }
}
