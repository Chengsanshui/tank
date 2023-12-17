package com.wjy_chy.tank.collision;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.wjy_chy.tank.GameConfig;
import com.wjy_chy.tank.GameType;
import com.wjy_chy.tank.ItemType;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;

/**
 * Collision between bullets and enemies (bullets from the enemy camp need to be ignored;
 * this ignored setting is written into the entity method that generates bullets)
 *      Destroy enemies and also destroy bullets
 */
public class BulletEnemyHandler extends CollisionHandler {

    public BulletEnemyHandler() {
        super(GameType.BULLET, GameType.ENEMY);
    }

    protected void onCollisionBegin(Entity bullet, Entity enemy) {
        play("normalBomb.wav");
        spawn("explode", enemy.getCenter().getX() - 25, enemy.getCenter().getY() - 20);
        bullet.removeFromWorld();
        enemy.removeFromWorld();
        inc("destroyedEnemy", 1);
        // get item. There is a certain chance of getting props
        if (FXGLMath.randomBoolean(GameConfig.SPAWN_ITEM_PRO)) {
            spawn("item",
                    new SpawnData(FXGLMath.random(50, getAppWidth() - 50 - 6 * 24)
                            , FXGLMath.random(50, getAppHeight() - 50))
                            .put("itemType", FXGLMath.random(ItemType.values()).get()));
        }
    }
}
