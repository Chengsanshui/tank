package com.wjy_chy.tank;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.time.TimerAction;
import com.wjy_chy.tank.collision.*;
import com.wjy_chy.tank.components.PlayerComponent;
import com.wjy_chy.tank.effects.HelmetEffect;
import com.wjy_chy.tank.ui.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;


public class TankApp extends GameApplication {

    private Entity player;
    private PlayerComponent playerComponent;
    private Random random = new Random();
    public LazyValue<FailedScene> failedSceneLazyValue = new LazyValue<>(FailedScene::new);
    private LazyValue<SuccessScene> successSceneLazyValue = new LazyValue<>(SuccessScene::new);

    /**
     * The three points at the top are used to spawn enemy tanks
     */
    private int[] enemySpawnX = {30, 295 + 30, 589 + 20};

    /**
     * Base plus fixed timer action
     */
    private TimerAction spadeTimerAction;
    /**
     * Timer action for enemy freeze gauges
     */
    private TimerAction freezingTimerAction;
    /**
     * Refresh enemy tanks at regular intervals
     */
    private TimerAction spawnEnemyTimerAction;

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(0.5);
        getSettings().setGlobalMusicVolume(0.5);
    }

    @Override
    protected void initSettings(GameSettings settings) {

        //Set the width of the interface and add a sidebar to the right
        settings.setWidth(28 * 24 + 6 * 24);
        settings.setHeight(28 * 24);
        settings.setTitle("2023 Tank");

        //Set the icon of the program
        settings.setAppIcon("ui/icon.png");
        settings.setVersion("Version 1.0");
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.getCSSList().add("tankApp.css");

        //Set the mouse hotspot, and offset
        settings.setDefaultCursor(new CursorInfo("ui/cursor.png", 0, 0));
        //Display of FPS, CPU, RAM and other information
        //settings.setProfilingEnabled(true);
        //Development mode: This can output a large number of log exception traces
        //settings.setApplicationMode(ApplicationMode.DEVELOPER);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public StartupScene newStartup(int width, int height) {
                //Customize the startup scenario
                return new GameStartupScene(width, height);
            }

            @Override
            public FXGLMenu newMainMenu() {
                //Main menu scene
                return new GameMainMenu();
            }

            @Override
            public LoadingScene newLoadingScene() {
                //Pre-game loading scene
                return new GameLoadingScene();
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        if (getFileSystemService().exists(GameConfig.CUSTOM_LEVEL_PATH)) {
            vars.put("level", 0);
        }else {
            vars.put("level", 1);
        }
        vars.put("playerBulletLevel", 1);
        vars.put("freezingEnemy", false);
        vars.put("destroyedEnemy", 0);
        vars.put("spawnedEnemy", 0);
        vars.put("gameOver", false);
    }

    @Override
    //Set the player to move up, down, left, and right and fire cannonballs
    protected void initInput() {
        onKey(KeyCode.W, this::moveUpAction);
        onKey(KeyCode.UP, this::moveUpAction);

        onKey(KeyCode.S, this::moveDownAction);
        onKey(KeyCode.DOWN, this::moveDownAction);

        onKey(KeyCode.A, this::moveLeftAction);
        onKey(KeyCode.LEFT, this::moveLeftAction);

        onKey(KeyCode.D, this::moveRightAction);
        onKey(KeyCode.RIGHT, this::moveRightAction);

        onKey(KeyCode.SPACE, this::shootAction);
        onKey(KeyCode.F, this::shootAction);
    }

    private boolean tankIsReady() {
        return player != null && playerComponent != null && !getb("gameOver") && player.isActive();
    }

    private void shootAction() {
        if (tankIsReady()) {
            playerComponent.shoot();
        }
    }

    private void moveRightAction() {
        if (tankIsReady()) {
            playerComponent.right();
        }
    }

    private void moveLeftAction() {
        if (tankIsReady()) {
            playerComponent.left();
        }
    }

    private void moveDownAction() {
        if (tankIsReady()) {
            playerComponent.down();
        }
    }

    private void moveUpAction() {
        if (tankIsReady()) {
            playerComponent.up();
        }
    }

    @Override
    protected void initGame() {
        //1.set background color to black
        getGameScene().setBackgroundColor(Color.BLACK);

        //2.Specify the factory class for creating game entities
        getGameWorld().addEntityFactory(new GameEntityFactory());

        buildAndStartLevel();
        getip("destroyedEnemy").addListener((ob, ov, nv) -> {
            if (nv.intValue() == GameConfig.ENEMY_AMOUNT) {
                set("gameOver", true);
                play("Win.wav");
                runOnce(
                        () -> getSceneService().pushSubScene(successSceneLazyValue.get()),
                        Duration.seconds(1.5));
            }
        });
    }

    public void buildAndStartLevel() {
        //1. Clean up the remnants of the previous level
        // (here the main thing is to clean up the sound residues)

        //Clean up the remnants of the level
        // (here the main thing is to clean up the sound residue)
        getGameWorld().getEntitiesByType(
                GameType.BULLET, GameType.ENEMY, GameType.PLAYER
        ).forEach(Entity::removeFromWorld);

        //2. Opening animation
        Rectangle rect1 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        Rectangle rect2 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        rect2.setLayoutY(getAppHeight() / 2.0);
        Text text = new Text("STAGE " + geti("level"));
        text.setFill(Color.WHITE);
        text.setFont(new Font(35));
        text.setLayoutX(getAppWidth() / 2.0 - 80);
        text.setLayoutY(getAppHeight() / 2.0 - 5);
        Pane p1 = new Pane(rect1, rect2, text);

        addUINode(p1);

        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(rect1.translateYProperty(), -getAppHeight() / 2.0),
                        new KeyValue(rect2.translateYProperty(), getAppHeight() / 2.0)
                ));
        tl.setOnFinished(e -> removeUINode(p1));

        PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
        pt.setOnFinished(e -> {
            text.setVisible(false);
            tl.play();
            //3. Start a new level
            startLevel();
        });
        pt.play();
    }

    private void startLevel() {
        if (spawnEnemyTimerAction != null) {
            spawnEnemyTimerAction.expire();
            spawnEnemyTimerAction = null;
        }
        set("gameOver", false);
        //Clear the remaining item effects from the previous level
        set("freezingEnemy", false);
        //Restore the number of enemy troops destroyed
        set("destroyedEnemy", 0);
        //Restores the number of enemy troops that spawn
        set("spawnedEnemy", 0);

        expireAction(freezingTimerAction);
        expireAction(spadeTimerAction);
        //If the level is 0, start the game with a user-defined map
       if (geti("level")==0){
           Level level;
           try {
               level = new TMXLevelLoader()
                       .load(new File(GameConfig.CUSTOM_LEVEL_PATH).toURI().toURL(), getGameWorld());
               getGameWorld().setLevel(level);
           } catch (MalformedURLException e) {
               throw new RuntimeException(e);
           }
       }else {
           setLevelFromMap("level" + geti("level") + ".tmx");
       }
        play("start.wav");
        player = null;
        player = spawn("player", 9 * 24 + 3, 25 * 24);
        //At the beginning of each round, the player's tank has an invincibility protection time
        player.getComponent(EffectComponent.class).startEffect(new HelmetEffect());
        playerComponent = player.getComponent(PlayerComponent.class);
        //A UI that displays information
        getGameScene().addGameView(new GameView(new InfoPane(), 100));
        //Start by spawning a few enemy tanks
        for (int i = 0; i < enemySpawnX.length; i++) {
            spawn("enemy",
                    new SpawnData(enemySpawnX[i], 30).put("assentName", "tank/E" + FXGLMath.random(1, 12) + "U.png"));
            inc("spawnedEnemy", 1);
        }
        spawnEnemy();
    }

    private void spawnEnemy() {
        if (spawnEnemyTimerAction != null) {
            spawnEnemyTimerAction.expire();
            spawnEnemyTimerAction = null;
        }
        //Entities used to detect collisions
        // (to prevent tanks from colliding with other tanks as soon as they are created, "getting entangled")
        Entity spawnBox = spawn("spawnBox", new SpawnData(-100, -100));

        //A timer used to spawn enemies, which periodically attempts to spawn enemy tanks,
        // but if the location of the enemy tank is occupied by other existing tanks, then the enemy tank will not spawn this time
        spawnEnemyTimerAction = run(() -> {
            //the number of attempts to spawn enemy tanks; 2 or 3 times
            int testTimes = FXGLMath.random(2, 3);
            for (int i = 0; i < testTimes; i++) {
                if (geti("spawnedEnemy") < GameConfig.ENEMY_AMOUNT) {
                    boolean canGenerate = true;
                    //One x coordinate of the array is randomly drawn
                    int x = enemySpawnX[random.nextInt(3)];
                    int y = 30;
                    spawnBox.setPosition(x, y);
                    List<Entity> tankList = getGameWorld().getEntitiesByType(GameType.ENEMY, GameType.PLAYER);
                    //If the position of the enemy tank that is about to spawn conflicts with the position of the existing tank,
                    // then no tank will be created here
                    for (Entity tank : tankList) {
                        if (tank.isActive() && spawnBox.isColliding(tank)) {
                            canGenerate = false;
                            break;
                        }
                    }
                    //If you can spawn enemy tanks, then spawn tanks
                    if (canGenerate) {
                        inc("spawnedEnemy", 1);
                        spawn("enemy",
                                new SpawnData(x, y).put("assentName", "tank/E" + FXGLMath.random(1, 12) + "U.png"));
                    }
                    //隐藏这个实体
                    spawnBox.setPosition(-100, -100);

                } else {
                    if (spawnEnemyTimerAction != null) {
                        spawnEnemyTimerAction.expire();
                    }
                }
            }
        }, GameConfig.SPAWN_ENEMY_TIME);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
        getPhysicsWorld().addCollisionHandler(new BulletPlayerHandler());
        BulletBrickHandler bulletBrickHandler = new BulletBrickHandler();
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler);
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler.copyFor(GameType.BULLET, GameType.STONE));
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler.copyFor(GameType.BULLET, GameType.GREENS));
        getPhysicsWorld().addCollisionHandler(new BulletFlagHandler());
        getPhysicsWorld().addCollisionHandler(new BulletBorderHandler());
        getPhysicsWorld().addCollisionHandler(new BulletBulletHandler());
        getPhysicsWorld().addCollisionHandler(new PlayerItemHandler());
    }

    public void freezingEnemy() {
        expireAction(freezingTimerAction);
        set("freezingEnemy", true);
        freezingTimerAction = runOnce(() -> {
            set("freezingEnemy", false);
        }, GameConfig.STOP_MOVE_TIME);
    }

    public void spadeBackUpBase() {
        expireAction(spadeTimerAction);
        //The upgrade base is surrounded by stone walls
        updateWall(true);
        spadeTimerAction = runOnce(() -> {
            //The walls around the base are reduced to brick walls
            updateWall(false);
        }, GameConfig.SPADE_TIME);
    }
    /**
     * Defenses around the base
     * According to the rules of the game: the default is a brick wall, and after eating the shovel,
     * it will be upgraded to a stone wall;
     */
    private void updateWall(boolean isStone) {
        //Cycle through the walls surrounding the base
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                if (row != 0 && (col == 1 || col == 2)) {
                    continue;
                }
                //Delete the old wall
                List<Entity> entityTempList = getGameWorld().getEntitiesAt(new Point2D(288 + col * 24, 576 + row * 24));
                for (Entity entityTemp : entityTempList) {
                    Serializable type = entityTemp.getType();
                    //If it is a player-built map, then you need to determine whether it is water,
                    // grass, snow, etc
                    if (type == GameType.STONE || type == GameType.BRICK || type == GameType.SNOW || type == GameType.SEA || type == GameType.GREENS) {
                        if (entityTemp.isActive()) {
                            entityTemp.removeFromWorld();
                        }
                    }
                }
                //Create a new wall
                if (isStone) {
                    spawn("itemStone", new SpawnData(288 + col * 24, 576 + row * 24));
                } else {
                    spawn("brick", new SpawnData(288 + col * 24, 576 + row * 24));
                }
            }
        }
    }

    /**
     * Let the Time Action expire
     */
    public void expireAction(TimerAction action) {
        if (action == null) {
            return;
        }
        if (!action.isExpired()) {
            action.expire();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
