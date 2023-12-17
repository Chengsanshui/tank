package com.wjy_chy.tank.components;

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
import com.wjy_chy.tank.GameConfig;
import com.wjy_chy.tank.effects.ShipEffect;
import com.wjy_chy.tank.GameType;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.spawn;

/**
 * Player behavior, movement and shooting
 */
public class PlayerComponent extends Component {

    //In order to prevent movements at diagonal upwards and downwards angles,

    private boolean movedThisFrame = false;
    private double speed = 0;
    private Vec2 velocity = new Vec2();
    private BoundingBoxComponent bbox;

    private LazyValue<EntityGroup> blocksAll = new LazyValue<>(() -> entity.getWorld().getGroup(GameType.BRICK, GameType.FLAG, GameType.SEA, GameType.STONE, GameType.ENEMY, GameType.BORDER_WALL));
    private LazyValue<EntityGroup> blocks = new LazyValue<>(() -> entity.getWorld().getGroup(GameType.BRICK, GameType.FLAG, GameType.STONE, GameType.ENEMY, GameType.BORDER_WALL));
    private LocalTimer shootTimer = FXGL.newLocalTimer();
    private Dir moveDir = Dir.UP;

    @Override
    /* When the game is running, .tpf will be called for each frame,
       which represents the time of this frame; generally speaking, 60 frames per second
     */
    //Distance traveled time * speed = distance
    public void onUpdate(double tpf) {
        speed = tpf * GameConfig.PLAYER_SPEED;
        movedThisFrame = false;
    }

    //Set the movement to the right and change the corresponding shell firing direction
    public void right() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(90);
        moveDir = Dir.RIGHT;
        move();
    }

    //Set the movement to the left and change the corresponding shell firing direction
    public void left() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(270);
        moveDir = Dir.LEFT;
        move();

    }

    //Set the moves downwards and changes the corresponding shell firing direction
    public void down() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(180);
        moveDir = Dir.DOWN;
        move();
    }

    //Set the moves upward and changes the corresponding shell firing direction
    public void up() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(0);
        moveDir = Dir.UP;
        move();
    }


    //Set the implementation of specific tank movement
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
            //Movement, retreat when encountering obstacles
            if (collision) {
                entity.translate(-velocity.x, -velocity.y);
                break;
            }
        }
    }



    //Set the location, direction, etc. of tank launch
    public void shoot() {

        //Set the interval between bullet firing to prevent a whole row of bullets from appearing
        if (!shootTimer.elapsed(GameConfig.PLAYER_SHOOT_DELAY)) {
            return;
        }

        //Pass in the direction, position and other parameters of the tank to the corresponding function,
        //and set the position where the bullet is fired from the middle of the tank.
        spawn("bullet", new SpawnData(getEntity().getCenter().add(-4, -4.5))
                .put("direction", moveDir.getVector())
                .put("owner", entity));
        shootTimer.capture();
    }
}
