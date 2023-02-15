package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

public class Running extends OnGround {

    @Inject
    public Running(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<KinematicBodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs,
            ComponentMapper<MovingBodyComponent> moving,
            ComponentMapper<TransformComponent> transforms,
            GameConfig config) {
        super(players, bodies, animated, inputs, moving, transforms, config);
    }

    @Override
    protected String animation() {
        return "run";
    }

    @Override
    public void groundUpdate(Entity entity) {
        if (!isMoving()) {
            PlayerComponent player = players.get(entity);
            player.stateMachine.changeState(player.idle);
        }
    }
}
