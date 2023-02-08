package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.harlequin.ui.AnimationStateMachine;

public abstract class HorizontalMovement extends CharacterState {
    private static final float VELOCITY = 4f;
    private float velocity = 0f;

    public HorizontalMovement(ComponentMapper<PlayerComponent> players, ComponentMapper<BodyComponent> bodies, ComponentMapper<AnimatedComponent> animated, ComponentMapper<InputComponent> inputs) {
        super(players, bodies, animated, inputs);
    }

    @Override
    public void update(Entity entity) {
        InputComponent input = inputs.get(entity);
        boolean goingLeft = input.leftPressed;
        boolean goingRight = input.rightPressed;
        if (goingLeft || goingRight) {
            velocity = goingLeft ? -VELOCITY : VELOCITY;
            AnimationStateMachine stateMachine = animated.get(entity).stateMachine;
            float scale = Math.abs(stateMachine.getScaleX());
            stateMachine.setScaleX(scale * (goingLeft ? -1 : 1));
        } else {
            velocity = 0f;
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
        Body body = bodies.get(entity).body;
        float yVelocity = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity, yVelocity);
    }

    protected final boolean isMoving() {
        return velocity != 0;
    }
}
