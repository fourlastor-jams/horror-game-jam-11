package io.github.fourlastor.game.level.unphysics;

import com.badlogic.gdx.math.Rectangle;

public class Transform {
    private final Rectangle area;

    public Transform(Rectangle area) {
        this.area = area;
    }

    public final void moveXBy(int amount) {
        area.x += amount;
    }

    public final void moveYBy(int amount) {
        area.y += amount;
    }

    public float left() {
        return area.x;
    }

    public float right() {
        return area.x + area.width;
    }

    public float top() {
        return area.y;
    }

    public float bottom() {
        return area.y + area.height;
    }

    public Rectangle area() {
        return area;
    }
}
