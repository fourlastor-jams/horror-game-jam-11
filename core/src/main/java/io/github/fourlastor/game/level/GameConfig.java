package io.github.fourlastor.game.level;

import com.badlogic.gdx.math.Vector2;

public class GameConfig {
    public final float width;
    public final float height;
    public final float scale;
    public final Vector2 gravity;

    public GameConfig(float width, float height, float scale, Vector2 gravity) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.gravity = gravity;
    }
}
