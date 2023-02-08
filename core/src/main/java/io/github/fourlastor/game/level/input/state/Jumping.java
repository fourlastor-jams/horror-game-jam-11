package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import javax.inject.Inject;

public class Jumping extends CharacterState {

    @Inject
    public Jumping(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs) {
        super(players, bodies, animated, inputs);
    }

    @Override
    protected String animation() {
        return "jump";
    }

    float timePassed = 0f;

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        timePassed = 0f;
    }

    @Override
    public void update(Entity entity) {
        timePassed += delta();
        if (timePassed > 1f) {
            PlayerComponent playerComponent = players.get(entity);
            playerComponent.stateMachine.changeState(playerComponent.idle);
        }
    }
}
