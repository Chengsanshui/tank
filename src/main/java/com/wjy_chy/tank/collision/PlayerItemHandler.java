package com.wjy_chy.tank.collision;

import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.wjy_chy.tank.GameConfig;
import com.wjy_chy.tank.GameType;
import com.wjy_chy.tank.ItemType;
import com.wjy_chy.tank.TankApp;
import com.wjy_chy.tank.effects.HelmetEffect;
import com.wjy_chy.tank.effects.ShipEffect;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Players get props
 *      The prop disappears and produces the prop effect
 */
public class PlayerItemHandler extends CollisionHandler {

    public PlayerItemHandler() {
        super(GameType.PLAYER, GameType.ITEM);
    }

    protected void onCollisionBegin(Entity player, Entity item) {
        TankApp app = getAppCast();
        ItemType itemType = item.getObject("itemType");
        play("item.wav");
        item.removeFromWorld();
        switch (itemType) {
            //Props: bombs; find all enemy tanks on the map and destroy them
            // (bullets can produce props when attacking enemies, but props cannot produce props when attacking enemies)
            case BOMB -> collisionBomb();
            //Props: Tank; Player recovers 1 health point
            case TANK -> collisionTank(player);
            //Props: Boat; players can drive on the water (the effect will not be carried over to the next level)
            case SHIP -> collisionShip(player);
            //Props: Star; if the player's bullet is not at full level, it will be upgraded.
            case STAR -> collisionStar();
            //道具:铁锨;基地周围的墙,升级到石头
            case SPADE -> app.spadeBackUpBase();
            //Props: Heart; the player's health is restored to the maximum value
            case HEART -> collisionHeart(player);
            //Props: timer; enemy tanks, all stop moving for a period of time
            case TIME -> app.freezingEnemy();
            //Props: weapons; player bullets are upgraded to full level (the effect will continue to the next level)
            case GUN -> set("playerBulletLevel", GameConfig.PLAYER_BULLET_MAX_LEVEL);
            //Props: Helmet; players gain invincible protection for a period of time
            case HELMET -> player.getComponent(EffectComponent.class)
                    .startEffect(new HelmetEffect());
            default -> {
            }
        }
    }

    private void collisionHeart(Entity player) {
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        hp.setValue(hp.getMaxValue());
    }

    private void collisionStar() {
        if (geti("playerBulletLevel") < GameConfig.PLAYER_BULLET_MAX_LEVEL) {
            inc("playerBulletLevel", 1);
        }
    }

    private void collisionShip(Entity player) {
        if (!player.getComponent(EffectComponent.class).hasEffect(ShipEffect.class)) {
            player.getComponent(EffectComponent.class).startEffect(new ShipEffect());
        }
    }

    private void collisionTank(Entity player) {
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        if (hp.getValue() < hp.getMaxValue()) {
            hp.damage(-1);
        }
    }

    private void collisionBomb() {
        List<Entity> enemyList = getGameWorld().getEntitiesByType(GameType.ENEMY);
        play("rocketBomb.wav");
        for (Entity enemy : enemyList) {
            spawn("explode", enemy.getCenter().getX() - 25, enemy.getCenter().getY() - 20);
            enemy.removeFromWorld();
            inc("destroyedEnemy", 1);
        }
    }

}
