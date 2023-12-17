package com.wjy_chy.tank.components;

import javafx.geometry.Point2D;


public enum Dir {
    /**
     * direction of movement
     */
    UP(new Point2D(0, -1)),
    RIGHT(new Point2D(1, 0)),
    DOWN(new Point2D(0, 1)),
    LEFT(new Point2D(-1, 0));

    public final Point2D vector;

    Dir(Point2D vector) {
        this.vector = vector;
    }

    public Point2D getVector() {
        return vector;
    }

}
