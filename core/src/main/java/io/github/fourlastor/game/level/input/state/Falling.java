package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;

abstract class Falling extends HorizontalMovement {

    protected final GameConfig config;

    private float fallingTime = 0f;
    private float attemptedTime = -1;

    public Falling(
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

    protected final float fallingTime() {
        return fallingTime;
    }

    @Override
    protected String animation() {
        return "fall";
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        fallingTime = 0f;
        attemptedTime = -1f;
        entity.getComponent(GravityComponent.class).gravity.scl(config.player.fallingGravityRatio);
    }

    @Override
    public void exit(Entity entity) {
        entity.getComponent(GravityComponent.class).gravity.scl(1 / config.player.fallingGravityRatio);
        super.exit(entity);
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        fallingTime += delta();
        if (inputs.get(entity).jumpJustPressed) {
            attemptedTime = fallingTime;
        }
        if (bodies.get(entity).touching.y == -1) {
            PlayerComponent playerComponent = players.get(entity);
            if (attemptedTime >= 0 && fallingTime - attemptedTime < config.player.fallingGraceTime) {
                playerComponent.stateMachine.changeState(playerComponent.jumping);
            } else {
                playerComponent.stateMachine.changeState(playerComponent.idle);
            }
        }
    }
}
