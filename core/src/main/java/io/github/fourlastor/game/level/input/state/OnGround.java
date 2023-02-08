package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;

public abstract class OnGround extends LateralMovement {

    public OnGround(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs) {
        super(players, bodies, animated, inputs);
    }

    @Override
    public final void update(Entity entity) {
        if (inputs.get(entity).jumpJustPressed) {
            PlayerComponent player = players.get(entity);
            player.stateMachine.changeState(player.jumping);
            return;
        }
        super.update(entity);
        groundUpdate(entity);
    }

    abstract void groundUpdate(Entity entity);
}
