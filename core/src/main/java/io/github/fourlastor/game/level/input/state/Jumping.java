package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

public class Jumping extends HorizontalMovement {

    private final GameConfig config;

    private float initialY;

    @Inject
    public Jumping(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<KinematicBodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs,
            ComponentMapper<MovingBodyComponent> moving,
            ComponentMapper<TransformComponent> transforms,
            GameConfig config) {
        super(players, bodies, animated, inputs, moving, transforms, config);
        this.config = config;
    }

    @Override
    protected String animation() {
        return "jump";
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        MovingBodyComponent movingBodyComponent = moving.get(entity);
        movingBodyComponent.speed.y = config.player.jumpSpeed;
        initialY = transforms.get(entity).transform.bottom();
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        MovingBodyComponent movingComponent = moving.get(entity);
        Transform transform = transforms.get(entity).transform;
        float distanceTravelled = Math.abs(transform.bottom() - initialY);
        if (bodies.get(entity).touching.y == 1) {
            movingComponent.speed.y = movingComponent.speed.y / 5f;
        }
        if (config.player.minJumpHeight <= distanceTravelled && !inputs.get(entity).jumpPressed) {
            movingComponent.speed.y = movingComponent.speed.y / 1.7f;
        }
        if (movingComponent.speed.y <= 0f) {
            PlayerComponent playerComponent = players.get(entity);
            playerComponent.stateMachine.changeState(playerComponent.fallingFromJump);
        }
    }
}
