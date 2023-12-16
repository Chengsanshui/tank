package com.leewyatt.github.tank;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.ui.ProgressBar;
import com.leewyatt.github.tank.components.EnemyComponent;
import com.leewyatt.github.tank.components.FlagViewComponent;
import com.leewyatt.github.tank.components.PlayerComponent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * 产生实体的工具类
 */
public class GameEntityFactory implements EntityFactory {

    @Spawns("player")
    //决定玩家的外观大小,等参数
    public Entity newPlayer(SpawnData data) {
        //生命值组件
        HealthIntComponent hpComponent = new HealthIntComponent(GameConfig.PLAYER_HEALTH);
        //进度条(生命值)
        ProgressBar hpView = new ProgressBar(false);
        hpView.setFill(Color.LIGHTGREEN);
        hpView.setMaxValue(GameConfig.PLAYER_HEALTH);
        hpView.setWidth(35);
        hpView.setHeight(8);
        hpView.setTranslateY(42);
        //进度条的值和生命值组件的值绑定
        hpView.currentValueProperty().bind(hpComponent.valueProperty());
        //生命值不同,生命值进度条颜色不同
        hpComponent.valueProperty().addListener((ob, ov, nv) -> {
            int hpValue = nv.intValue();
            if (hpValue >= GameConfig.PLAYER_HEALTH * 0.7) {
                hpView.setFill(Color.LIGHTGREEN);
            } else if (hpValue >= GameConfig.PLAYER_HEALTH * 0.4) {
                hpView.setFill(Color.GOLD);
            } else {
                hpView.setFill(Color.RED);
            }
        });
        return FXGL.entityBuilder(data)
                .type(GameType.PLAYER)
                .bbox(BoundingShape.box(39, 39))
                .view("tank/H1U.png")
                .view(hpView)
                .with(new EffectComponent())
                .with(hpComponent)
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("enemy")
    //决定敌人的外观和大小
    public Entity newEnemy(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.ENEMY)
                .bbox(BoundingShape.box(39, 39))
                .with(new EnemyComponent())
                .collidable()
                .build();
    }

    @Spawns("flag")
    //决定flag的外观和大小
    public Entity newFlag(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.FLAG)
                .bbox(BoundingShape.box(48, 48))
                .with(new FlagViewComponent())
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("brick")
    //设置brick的参数,tank不可以穿过
    public Entity newBrick(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.BRICK)
                .viewWithBBox("map/brick.png")
                .bbox(BoundingShape.box(24, 24))
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("greens")
    //设置greens的参数,index 很高,可以把tank覆盖住
    public Entity newGreens(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.GREENS)
                .bbox(BoundingShape.box(24, 24))
                .zIndex(100)
                .neverUpdated()
                .collidable()
                .build();
    }

    private final AnimationChannel seaAnimChan = new AnimationChannel(FXGL.image("map/sea_anim.png"), Duration.seconds(1.5), 2);

    @Spawns("sea")
    //设置水面的参数,并且定时替换水面的图片造成动态的效果
    public Entity newSea(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.SEA)
                .viewWithBBox(new AnimatedTexture(seaAnimChan).loop())
                .build();
    }

    @Spawns("snow")
    public Entity newSnow(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.SNOW)
                .bbox(BoundingShape.box(24, 24))
                .neverUpdated()
                .build();
    }

    private static final List<Image> STONE_BRICK_FLASH_IMAGE_LIST = new ArrayList<>();
    private static final AnimationChannel STONE_BRICK_AC;

    static {
        STONE_BRICK_FLASH_IMAGE_LIST.add(FXGL.image("map/stone.png"));
        STONE_BRICK_FLASH_IMAGE_LIST.add(FXGL.image("map/brick.png"));
        STONE_BRICK_AC = new AnimationChannel(STONE_BRICK_FLASH_IMAGE_LIST, Duration.seconds(.5));
    }

    @Spawns("stone")
    public Entity newStone(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.STONE)
                .bbox(BoundingShape.box(24,24))
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("itemStone")
    public Entity newItemStone(SpawnData data) {
        AnimatedTexture animatedTexture = new AnimatedTexture(STONE_BRICK_AC);
        Entity entity = FXGL.entityBuilder(data)
                .type(GameType.STONE)
                .viewWithBBox(animatedTexture)
                .collidable()
                .build();
        //在最后的几秒,会闪烁提示玩家, 基地的保护时间即将过期
        FXGL.runOnce(animatedTexture::loop, GameConfig.SPADE_NORMAL_TIME);
        return entity;
    }

    @Spawns("bullet")
    //设置子弹的形态,方向,以及速度,以及子弹遇到其他物体的的处理等等
    public Entity newBullet(SpawnData data) {
        double speed;
        String textureStr;
        Entity owner = data.get("owner");
        CollidableComponent collidableComponent = new CollidableComponent(true);
        //检测碰撞, 忽略同类;Detect collisions, ignore the same type;
        collidableComponent.addIgnoredType(owner.getType());
        if (GameType.PLAYER == owner.getType()) {
            int bulletLevel = FXGL.geti("playerBulletLevel");
            if (bulletLevel < GameConfig.PLAYER_BULLET_MAX_LEVEL) {
                textureStr = "bullet/normal.png";
                FXGL.play("normalFire.wav");
            } else {
                textureStr = "bullet/plus.png";
                FXGL.play("rocketFire.wav");
            }
            speed = GameConfig.PLAYER_BULLET_SPEED + bulletLevel * 60;
        } else {
            speed = GameConfig.ENEMY_BULLET_SPEED;
            textureStr = "bullet/normal.png";
            FXGL.play("normalFire.wav");
        }
        return FXGL.entityBuilder(data)
                .type(GameType.BULLET)
                .viewWithBBox(textureStr)
                .with(collidableComponent)
                .with(new ProjectileComponent(data.get("direction"), speed))
                .build();
    }

    private final Duration explodeAnimeTime = Duration.seconds(0.5);
    private  final AnimationChannel explodeAc = new AnimationChannel(FXGL.image("explode/explode_level_2.png"), explodeAnimeTime,9);

    @Spawns("explode")
    public Entity newExplode(SpawnData data) {
        return FXGL.entityBuilder(data)
                .view(new AnimatedTexture(explodeAc).play())
                .with(new ExpireCleanComponent(explodeAnimeTime))
                .build();
    }

    @Spawns("spawnBox")
    public Entity newSpawnBox(SpawnData data) {
        return FXGL.entityBuilder(data)
                .at(data.getX() - 5, data.getY() - 5)
                .bbox(BoundingShape.box(50, 50))
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("item")
    public Entity newItem(SpawnData data) {
        //1帧图片. 产生闪烁的效果,有"投机取巧"之嫌,以后可能使用2帧的图片替换
        AnimationChannel ac = new AnimationChannel(FXGL.image("item/" + data.<ItemType>get("itemType").toString().toLowerCase() + ".png"), 1, 30, 28, Duration.seconds(.5), 0, 1);
        AnimatedTexture animatedTexture = new AnimatedTexture(ac);
        Entity entity = FXGL.entityBuilder(data)
                .type(GameType.ITEM)
                .viewWithBBox(animatedTexture)
                .scale(1.2, 1.2)
                .with(new ExpireCleanComponent(GameConfig.ITEM_SHOW_TIME))
                .collidable()
                .zIndex(200)
                .build();
        FXGL.runOnce(animatedTexture::loop, GameConfig.ITEM_NORMAL_SHOW_TIME);
        return entity;
    }

    @Spawns("borderWall")
    //创建界面的边界,tanks不能走出边界,子弹对边界无效
    public Entity newBorderWall(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.BORDER_WALL)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.web("#666666")))
                .neverUpdated()
                .collidable()
                .build();
    }
}
