package ru.spbau.blackout.ingameui.settings;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.units.Rpx;

/**
 * Contains some settings for displaying of this UI object.
 * All getters and setters work with RPX.
 */
public class AbilityIconSettings {
    private int abilityNum;
    public int getAbilityNum() { return this.abilityNum; }
    public void setAbilityNum(int abilityNum) { this.abilityNum = abilityNum; }

    public Vector2 start = new Vector2(1100, 400);  // FIXME
    public void setStart(Vector2 start) { this.start = start; }
    public Vector2 getStart() { return this.start; }

    public AbilityIconSettings(int abilityNum) {
        this.abilityNum = abilityNum;
    }
}
