package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.PlayerComponent;
import javax.inject.Inject;

public class Idle extends OnGround {

    @Inject
    public Idle(StateMappers mappers, GameConfig config) {
        super(mappers, config);
    }

    @Override
    protected String animation() {
        return "idle";
    }

    @Override
    public void groundUpdate(Entity entity) {
        if (isMoving()) {
            PlayerComponent player = players.get(entity);
            player.stateMachine.changeState(player.running);
        }
    }
}
