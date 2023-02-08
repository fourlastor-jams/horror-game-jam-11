package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;

abstract class Falling extends HorizontalMovement {


    public Falling(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs) {
        super(players, bodies, animated, inputs);
    }

    @Override
    protected String animation() {
        return "fall";
    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        if (telegram.message == Message.PLAYER_ON_GROUND.ordinal()) {
            PlayerComponent playerComponent = players.get(entity);
            playerComponent.stateMachine.changeState(playerComponent.idle);
            return true;
        }
        return super.onMessage(entity, telegram);
    }
}
