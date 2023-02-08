package io.github.fourlastor.game.level;

import com.badlogic.gdx.math.Vector2;

public class GameConfig {
    public final float width;
    public final float height;
    public final float scale;
    public final Vector2 gravity;

    public final float movementSpeed;
    public final float jumpHeight;
    public final float fallingGraceTime;

    public GameConfig(float width, float height, float scale, Vector2 gravity, float movementSpeed, float jumpHeight, float fallingGraceTime) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.gravity = gravity;
        this.movementSpeed = movementSpeed;
        this.jumpHeight = jumpHeight;
        this.fallingGraceTime = fallingGraceTime;
    }
}
