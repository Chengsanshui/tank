package com.wjy_chy.tank.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.wjy_chy.tank.GameConfig;
import com.wjy_chy.tank.GameType;

import java.io.Serializable;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Bullets collide with walls; this method can also be used for bullets and stones, bullets and grass
 * Because the bullet may hit the intersection of two objects;
 * Therefore, it is necessary to judge at the same time how many objects the bullet collided with;
 * depending on the objects, different processes are performed.
 * If it hits grass, the bullet will not be destroyed;
 * If it is a top-level bullet, then the grass (forest) will be destroyed, but the bullet will still not be destroyed
 * If you hit a brick wall, the bullets and the wall will disappear.
 * If it hits a stone, the bullet will disappear
 * If it is a top bullet, the stone will also disappear
 */
public class BulletBrickHandler extends CollisionHandler {

    public BulletBrickHandler() {
        super(GameType.BULLET, GameType.BRICK);
    }

    /**
     * Note that this is not onCollisionBegin, because there are more collision detections to be checked.
     */
    @Override
    protected void onCollision(Entity bullet, Entity brick) {
        Entity tank = bullet.getObject("owner");
        Serializable tankType = tank.getType();
        //Find the colliding blocks (stones, earth walls, grass)
        List<Entity> list = getGameWorld().getEntitiesFiltered(tempE ->
                tempE.getBoundingBoxComponent().isCollidingWith(bullet.getBoundingBoxComponent())
                        && (tempE.isType(GameType.STONE)
                        || tempE.isType(GameType.BRICK)
                        || tempE.isType(GameType.GREENS))
        );
        boolean removeBullet = false;
        for (Entity entity : list) {
            Serializable entityType = entity.getType();
            if (entityType == GameType.BRICK) {
                removeBullet = true;
                if (entity.isActive()) {
                    entity.removeFromWorld();
                }
            } else if (entityType == GameType.GREENS) {
                if (tankType == GameType.PLAYER
                        && entity.isActive()
                        && geti("playerBulletLevel") == GameConfig.PLAYER_BULLET_MAX_LEVEL) {
                    entity.removeFromWorld();
                }
            } else { //STONE
                removeBullet = true;
                if (tankType == GameType.PLAYER
                        && entity.isActive()
                        && geti("playerBulletLevel") == GameConfig.PLAYER_BULLET_MAX_LEVEL) {
                    entity.removeFromWorld();
                }
            }
        }
        if (removeBullet) {
            bullet.removeFromWorld();
            play("normalBomb.wav");
            spawn("explode", bullet.getCenter().getX() - 25, bullet.getCenter().getY() - 20);
        }
    }
}
