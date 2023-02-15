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

public abstract class OnGround extends HorizontalMovement {

    public OnGround(
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
    public final void update(Entity entity) {
        if (inputs.get(entity).jumpJustPressed) {
            PlayerComponent player = players.get(entity);
            player.stateMachine.changeState(player.jumping);
            return;
        }
        if (bodies.get(entity).touching.y != -1) {
            PlayerComponent player = players.get(entity);
            player.stateMachine.changeState(player.fallingFromGround);
            return;
        }
        super.update(entity);
        groundUpdate(entity);
    }

    abstract void groundUpdate(Entity entity);
}
