package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;

import javax.inject.Inject;

public class FallingFromGround extends Falling {
    @Inject
    public FallingFromGround(ComponentMapper<PlayerComponent> players, ComponentMapper<BodyComponent> bodies, ComponentMapper<AnimatedComponent> animated, ComponentMapper<InputComponent> inputs) {
        super(players, bodies, animated, inputs);
    }
}
