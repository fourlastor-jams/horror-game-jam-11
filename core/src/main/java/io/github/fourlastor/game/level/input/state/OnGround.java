package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.PlayerComponent;

public abstract class OnGround extends HorizontalMovement {

    public OnGround(StateMappers mappers, GameConfig config) {
        super(mappers, config);
    }

    @Override
    public final void update(Entity entity) {
        if (inputs.get(entity).jumpJustPressed) {
            PlayerComponent player = players.get(entity);
            player.stateMachine.changeState(player.jumping);
            return;
        }
        if (bodies.get(entity).touching.y != 1) {
            PlayerComponent player = players.get(entity);
            player.stateMachine.changeState(player.fallingFromGround);
            return;
        }
        super.update(entity);
        groundUpdate(entity);
    }

    abstract void groundUpdate(Entity entity);
}
