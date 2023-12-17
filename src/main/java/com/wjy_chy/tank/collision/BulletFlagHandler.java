package com.wjy_chy.tank.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.wjy_chy.tank.GameType;
import com.wjy_chy.tank.TankApp;
import com.wjy_chy.tank.components.FlagViewComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Bullets do not distinguish between camps. As long as they hit the flag,
 * it will be immediately judged as a failure.
 */
public class BulletFlagHandler extends CollisionHandler {

    public BulletFlagHandler() {
        super(GameType.BULLET, GameType.FLAG);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity flag) {
        if (!getb("gameOver")) {
            FlagViewComponent flagComponent = flag.getComponent(FlagViewComponent.class);
            flagComponent.hitFlag();
            play("normalBomb.wav");
            spawn("explode", bullet.getCenter().getX() - 25, bullet.getCenter().getY() - 20);
            bullet.removeFromWorld();
            TankApp app = getAppCast();
            if (!getb("gameOver")) {
                set("gameOver", true);
                getSceneService().pushSubScene(app.failedSceneLazyValue.get());
            }
        }
    }
}
