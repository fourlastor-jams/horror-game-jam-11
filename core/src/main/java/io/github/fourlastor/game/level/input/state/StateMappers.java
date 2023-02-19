package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

public class StateMappers {
    public final ComponentMapper<PlayerComponent> players;
    public final ComponentMapper<KinematicBodyComponent> bodies;
    public final ComponentMapper<MovingBodyComponent> moving;
    public final ComponentMapper<TransformComponent> transforms;
    public final ComponentMapper<AnimatedComponent> animated;
    public final ComponentMapper<InputComponent> inputs;
    public final ComponentMapper<GravityComponent> gravity;

    @Inject
    public StateMappers(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<KinematicBodyComponent> bodies,
            ComponentMapper<MovingBodyComponent> moving,
            ComponentMapper<TransformComponent> transforms,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs,
            ComponentMapper<GravityComponent> gravity) {
        this.players = players;
        this.bodies = bodies;
        this.moving = moving;
        this.transforms = transforms;
        this.animated = animated;
        this.inputs = inputs;
        this.gravity = gravity;
    }
}
