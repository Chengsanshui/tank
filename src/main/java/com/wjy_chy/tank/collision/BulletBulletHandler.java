package com.wjy_chy.tank.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.wjy_chy.tank.GameType;

import java.io.Serializable;

/**
 * When bullets collide, as long as they are from different camps, both bullets will disappear.
 */
public class BulletBulletHandler extends CollisionHandler {
    public BulletBulletHandler() {
        super(GameType.BULLET, GameType.BULLET);
    }
    @Override
    protected void onCollisionBegin(Entity bullet1, Entity bullet2) {
        Entity owner1 = bullet1.getObject("owner");
        Serializable type1 = owner1.getType();

        Entity owner2 = bullet2.getObject("owner");
        Serializable type2 = owner2.getType();
        if (type1 != type2) {
            bullet1.removeFromWorld();
            bullet2.removeFromWorld();
        }
    }
}
