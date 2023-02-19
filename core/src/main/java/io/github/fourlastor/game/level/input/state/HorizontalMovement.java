package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import io.github.fourlastor.game.level.Area;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.harlequin.ui.AnimationStateMachine;

public abstract class HorizontalMovement extends CharacterState {
    private final GameConfig config;
    private float velocity = 0f;

    public HorizontalMovement(StateMappers mappers, GameConfig config) {
        super(mappers);
        this.config = config;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        InputComponent input = inputs.get(entity);
        PlayerComponent playerComponent = players.get(entity);
        if (playerComponent.area == Area.LADDER && input.upPressed) {
            playerComponent.stateMachine.changeState(playerComponent.onLadder);
            return;
        }
        if (input.movementChanged) {
            playerComponent.movementTime = 0f;
        }
        playerComponent.movementTime += delta();
        float progress = Math.min(1f, playerComponent.movementTime / config.player.accelerationTime);
        boolean goingLeft = input.leftPressed;
        boolean goingRight = input.rightPressed;
        if (goingLeft || goingRight) {
            float target = goingLeft ? -config.player.movementSpeed : config.player.movementSpeed;
            velocity = Interpolation.pow2.apply(progress) * target;
            AnimationStateMachine stateMachine = animated.get(entity).stateMachine;
            float scale = Math.abs(stateMachine.getScaleX());
            stateMachine.setScaleX(scale * (goingLeft ? -1 : 1));
        } else {
            float target = Math.signum(velocity) * config.player.movementSpeed;
            velocity = Interpolation.pow2.apply(1 - progress) * target;
        }
        updateBodyVelocity(entity);
    }

    @Override
    public void exit(Entity entity) {
        velocity = 0f;
        updateBodyVelocity(entity);
        super.exit(entity);
    }

    protected void updateBodyVelocity(Entity entity) {
        MovingBodyComponent body = moving.get(entity);
        body.speed.x = velocity;
    }

    protected final boolean isMoving() {
        return velocity != 0;
    }
}
