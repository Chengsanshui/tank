package com.wjy_chy.tank;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;


public class GameConfig {

    private GameConfig() {
    }

    private static final PropertyMap map;

    //There are detailed comments in the configuration file.
    static {
        map = FXGL.getAssetLoader().loadPropertyMap("properties/game.properties");
    }

    public static final int MAX_LEVEL = map.getInt("maxLevel");

    /**
     * Top bullets can destroy trees and hit stone walls
     */
    public static final int PLAYER_BULLET_MAX_LEVEL = map.getInt("bulletMaxLevel");
    /**
     * Number of enemy tanks
     */
    public static final int ENEMY_AMOUNT = map.getInt("enemyAmount");
    /**
     * player health
     */
    public static final int PLAYER_HEALTH = map.getInt("playerHealth");
    /**
     * Player bullet speed (base speed + bullet level * 60)
     */
    public static final int PLAYER_BULLET_SPEED = map.getInt("playerBulletSpeed");
    /**
     * Enemy bullet speed
     */
    public static final int ENEMY_BULLET_SPEED = map.getInt("enemyBulletSpeed");
    /**
     * Player shooting interval
     */
    public static final Duration PLAYER_SHOOT_DELAY = Duration.seconds(map.getDouble("playerShootDelay"));
    /**
     * Enemy shooting interval
     */
    public static final Duration ENEMY_SHOOT_DELAY = Duration.seconds(map.getDouble("enemyShootDelay"));
    /**
     * Invincible time protected by protective cover
     */
    public static final Duration HELMET_TIME = Duration.seconds(map.getDouble("helmetTime"));
    /**
     * Timing props. The time when the enemy stops moving.
     */
    public static final Duration STOP_MOVE_TIME = Duration.seconds(map.getDouble("stopMoveTime"));
    /**
     * The time when the prop appears
     */
    public static final Duration ITEM_SHOW_TIME = Duration.seconds(map.getDouble("itemShowTime"));
    /**
     * The time from when a prop appears to when it flashes as a reminder
     */
    public static final Duration ITEM_NORMAL_SHOW_TIME = Duration.seconds(map.getDouble("itemNormalShowTime"));
    /**
     * spade time to protect the base
     */
    public static final Duration SPADE_TIME = Duration.seconds(map.getDouble("spadeTime"));

    /**
     * The shovel is about to end, and the flashing prompt is 15 seconds - 12 seconds = 3 seconds.
     * In the last 3 seconds, flashing prompts will appear around the base.
     */
    public static final Duration SPADE_NORMAL_TIME = Duration.seconds(map.getDouble("spadeNormalTime"));

    /**
     * The interval between spawning local tanks
     */
    public static final Duration SPAWN_ENEMY_TIME = Duration.seconds(map.getDouble("spawnEnemyTime"));

    /**
     * Proportion of items produced
     */
    public static final double SPAWN_ITEM_PRO = map.getDouble("spawnItemPro");
    /**
     * player movement speed
     */
    public static final int PLAYER_SPEED = map.getInt("playerSpeed");
    /**
     * enemy movement speed
     */
    public static final int ENEMY_SPEED = map.getInt("enemySpeed");
    /**
     * Custom map tmx file path
     */
    public static final String CUSTOM_LEVEL_PATH = map.getString("customLevelPath");
    /**
     * Custom map data file path
     */
    public static final String CUSTOM_LEVEL_DATA = map.getString("customLevelData");

}
