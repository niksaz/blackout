package ru.spbau.blackout.database;

/**
 * Constants for generic work with MongoDB database.
 */
public class Database {

    public static final String DATABASE_NAME = "blackout";

    public static final String LOAD_COMMAND = "/load";
    public static final String UPGRADE_COMMAND = "/upgrade";
    public static final String COINS_EARNED = "coins";
    public static final String SETTINGS_SYNCHRONIZE_COMMAND = "/settings";

    public static final String HEALTH_UPGRADE = "health";
    public static final int HEALTH_UPGRADE_COST = 100;
    public static final int HEALTH_UPGRADE_PER_LEVEL = 10;

    public static final String ABILITY_UPGRADE = "ability";
    public static final int ABILITY_UPGRADE_COST_PER_LEVEL = 100;

    public static final int COINS_PER_WIN = 500;

    private Database() {}
}
