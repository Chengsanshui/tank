package com.leewyatt.github.tank;

import java.io.Serializable;


public enum GameType implements Serializable {
    /**
     * 游戏实体枚举类,方便以后的调用
     */
    BRICK, GREENS, FLAG, SEA, SNOW, STONE, PLAYER, ENEMY, BULLET, ITEM, BORDER_WALL, EMPTY
}
