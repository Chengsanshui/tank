package com.wjy_chy.tank.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.wjy_chy.tank.GameType;

/**
 * Collision detection: When the bullet collides with the boundary, the bullet disappears,
 * but no explosion effect or sound is produced, because there are too many sounds and explosion effects,
 * which makes it uncomfortable.
 */
public class BulletBorderHandler extends CollisionHandler {

    public BulletBorderHandler() {
        super(GameType.BULLET, GameType.BORDER_WALL);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity border) {
        bullet.removeFromWorld();
    }
}
