package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.PlayerComponent;

abstract class Falling extends HorizontalMovement {

    protected final GameConfig config;

    private float fallingTime = 0f;
    private float attemptedTime = -1;

    public Falling(StateMappers mappers, GameConfig config) {
        super(mappers, config);
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
        gravities.get(entity).gravity.scl(config.player.fallingGravityRatio);
    }

    @Override
    public void exit(Entity entity) {
        gravities.get(entity).gravity.scl(1 / config.player.fallingGravityRatio);
        super.exit(entity);
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        fallingTime += delta();
        if (inputs.get(entity).jumpJustPressed) {
            attemptedTime = fallingTime;
        }
        if (bodies.get(entity).touching.y == 1) {
            PlayerComponent playerComponent = players.get(entity);
            if (attemptedTime >= 0 && fallingTime - attemptedTime < config.player.fallingGraceTime) {
                playerComponent.stateMachine.changeState(playerComponent.jumping);
            } else {
                playerComponent.stateMachine.changeState(playerComponent.idle);
            }
        }
    }
}
