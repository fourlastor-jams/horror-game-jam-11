package io.github.fourlastor.game.level.input.state;

import io.github.fourlastor.game.level.GameConfig;
import javax.inject.Inject;

public class FallingFromJump extends Falling {
    @Inject
    public FallingFromJump(StateMappers mappers, GameConfig config) {
        super(mappers, config);
    }
}
