package io.github.fourlastor.game.level.unphysics;

import com.badlogic.gdx.math.Rectangle;

public class Transform {
    private final Rectangle area;

    public Transform(Rectangle area) {
        this.area = area;
    }

    public final void moveXBy(float amount) {
        area.x += amount;
    }

    public final void moveYBy(float amount) {
        area.y += amount;
    }

    public float left() {
        return area.x;
    }

    public float right() {
        return area.x + area.width;
    }

    public float bottom() {
        return area.y;
    }

    public float top() {
        return area.y + area.height;
    }

    public Rectangle area() {
        return area;
    }
}
