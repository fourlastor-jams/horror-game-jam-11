package io.github.fourlastor.game.level;

import com.badlogic.gdx.math.Vector2;

public class GameConfig {

    public final Display display;
    public final Physics physics;
    public final Player player;
    public final Entities entities;

    public GameConfig(Display display, Physics physics, Player player, Entities entities) {
        this.display = display;
        this.physics = physics;
        this.player = player;
        this.entities = entities;
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
        public final float accelerationTime;
        public final float minJumpHeight;
        public final float maxJumpHeight;
        public final float jumpSpeed;
        public final float fallingGraceTime;
        public final float fallingGravityRatio;
        public final float ladderSpeed;

        public Player(
                float movementSpeed,
                float accelerationTime,
                float minJumpHeight,
                float maxJumpHeight,
                float jumpSpeed,
                float fallingGraceTime,
                float fallingGravityRatio,
                float ladderSpeed) {
            this.movementSpeed = movementSpeed;
            this.accelerationTime = accelerationTime;
            this.minJumpHeight = minJumpHeight;
            this.maxJumpHeight = maxJumpHeight;
            this.jumpSpeed = jumpSpeed;
            this.fallingGraceTime = fallingGraceTime;
            this.fallingGravityRatio = fallingGravityRatio;
            this.ladderSpeed = ladderSpeed;
        }
    }

    public static class Entities {
        public final float spikeSizeRatio;

        public Entities(float spikeSizeRatio) {
            this.spikeSizeRatio = spikeSizeRatio;
        }
    }
}
