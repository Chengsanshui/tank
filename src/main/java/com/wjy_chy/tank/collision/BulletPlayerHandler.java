package com.wjy_chy.tank.collision;

import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.wjy_chy.tank.GameType;
import com.wjy_chy.tank.TankApp;
import com.wjy_chy.tank.effects.HelmetEffect;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Bullets collide with players (in order to expand multiple players, bullets from allies are ignored here,
 * friendly forces cannot accidentally hurt; the same ignored code is in the method of generating bullet entities)
 * The bullet disappears and the player's health is reduced.
 */
public class BulletPlayerHandler extends CollisionHandler {

    public BulletPlayerHandler() {
        super(GameType.BULLET, GameType.PLAYER);
    }

    protected void onCollisionBegin(Entity bullet, Entity player) {
        play("normalBomb.wav");
        if (player.getComponent(EffectComponent.class).hasEffect(HelmetEffect.class)) {
            bullet.removeFromWorld();
            return;
        }
        spawn("explode", bullet.getCenter().getX() - 25, bullet.getCenter().getY() - 20);
        bullet.removeFromWorld();
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        hp.damage(1);
        TankApp tankApp = getAppCast();
        if (hp.isZero()) {
            if (!getb("gameOver")) {
                player.removeFromWorld();
                set("gameOver", true);
                getSceneService().pushSubScene(tankApp.failedSceneLazyValue.get());
            }
        }
    }
}
