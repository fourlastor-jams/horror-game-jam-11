package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.PlayerComponent;
import javax.inject.Inject;

public class FallingFromGround extends Falling {
    @Inject
    public FallingFromGround(StateMappers mappers, GameConfig config) {
        super(mappers, config);
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        if (inputs.get(entity).jumpJustPressed && fallingTime() < config.player.fallingGraceTime) {
            PlayerComponent playerComponent = players.get(entity);
            playerComponent.stateMachine.changeState(playerComponent.jumping);
        }
    }
}
