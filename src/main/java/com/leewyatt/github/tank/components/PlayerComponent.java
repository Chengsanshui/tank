package com.leewyatt.github.tank.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.leewyatt.github.tank.GameConfig;
import com.leewyatt.github.tank.effects.ShipEffect;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.leewyatt.github.tank.GameType.*;

/**
 *
 * 玩家的行为,移动和射击
 */
public class PlayerComponent extends Component {

     //为了防止出现斜向上,斜向下等角度的移动,

    private boolean movedThisFrame = false;
    private double speed = 0;
    private Vec2 velocity = new Vec2();
    private BoundingBoxComponent bbox;

    private LazyValue<EntityGroup> blocksAll = new LazyValue<>(() -> entity.getWorld().getGroup(BRICK, FLAG, SEA, STONE, ENEMY, BORDER_WALL));
    private LazyValue<EntityGroup> blocks = new LazyValue<>(() -> entity.getWorld().getGroup(BRICK, FLAG, STONE, ENEMY, BORDER_WALL));
    private LocalTimer shootTimer = FXGL.newLocalTimer();
    private Dir moveDir = Dir.UP;

    @Override
    //游戏运行时,每一帧都会调用.tpf,代表这一帧的用时;一般来说,60帧每一秒
    //移动的距离 时间 * 速度 = 距离
    public void onUpdate(double tpf) {
        speed = tpf * GameConfig.PLAYER_SPEED;
        movedThisFrame = false;
    }

    //设置向右移动,并且改变相应的炮弹发射方向
    public void right() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(90);
        moveDir = Dir.RIGHT;
        move();
    }

    //设置向左移动,并且改变相应的炮弹发射方向
    public void left() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(270);
        moveDir = Dir.LEFT;
        move();

    }

    //设置向下移动,并且改变相应的炮弹发射方向
    public void down() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(180);
        moveDir = Dir.DOWN;
        move();
    }

    //设置向=上移动,并且改变相应的炮弹发射方向
    public void up() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(0);
        moveDir = Dir.UP;
        move();
    }


    //设置具体tank的移动的实现
    private void move() {
        if (!getEntity().isActive()) {
            return;
        }
        velocity.set((float) (moveDir.getVector().getX()*speed), (float) (moveDir.getVector().getY()*speed));
        int length = Math.round(velocity.length());
        velocity.normalizeLocal();
        List<Entity> blockList;
        if (entity.getComponent(EffectComponent.class).hasEffect(ShipEffect.class)) {
            blockList = blocks.get().getEntitiesCopy();
        } else {
            blockList = blocksAll.get().getEntitiesCopy();
        }
        for (int i = 0; i < length; i++) {
            entity.translate(velocity.x, velocity.y);
            boolean collision = false;
            for (int j = 0; j < blockList.size(); j++) {
                if (blockList.get(j).getBoundingBoxComponent().isCollidingWith(bbox)) {
                    collision = true;
                    break;
                }
            }
            //运动, 遇到障碍物回退
            if (collision) {
                entity.translate(-velocity.x, -velocity.y);
                break;
            }
        }
    }



    //设置tank发射的位置,方向等等
    public void shoot() {

        //设置子弹发射的间隔,防止出现一整行的子弹
        if (!shootTimer.elapsed(GameConfig.PLAYER_SHOOT_DELAY)) {
            return;
        }

        //传入tank的方向,位置等参数给对应的函数,设置子弹发射的位置是从tank的中间发射出去的
        spawn("bullet", new SpawnData(getEntity().getCenter().add(-4, -4.5))
                .put("direction", moveDir.getVector())
                .put("owner", entity));
        shootTimer.capture();
    }
}
