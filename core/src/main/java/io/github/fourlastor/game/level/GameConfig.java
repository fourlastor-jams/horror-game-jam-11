package io.github.fourlastor.game.level;

import com.badlogic.gdx.math.Vector2;

public class GameConfig {

    public final Display display;
    public final Physics physics;
    public final Player player;

    public GameConfig(Display display, Physics physics, Player player) {
        this.display = display;
        this.physics = physics;
        this.player = player;
    }

    public static class Physics {
        public final Vector2 gravity;

        public Physics(Vector2 gravity) {
            this.gravity = gravity;
        }
    }

    public static class Display {
        public final float width;
        public final float height;
        public final float scale;

        public Display(float width, float height, float scale) {
            this.width = width;
            this.height = height;
            this.scale = scale;
        }
    }

    public static class Player {

        public final float movementSpeed;
        public final float jumpHeight;
        public final float fallingGraceTime;

        public Player(float movementSpeed, float jumpHeight, float fallingGraceTime) {
            this.movementSpeed = movementSpeed;
            this.jumpHeight = jumpHeight;
            this.fallingGraceTime = fallingGraceTime;
        }
    }
}
