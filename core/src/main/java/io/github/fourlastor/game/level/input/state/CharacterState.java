package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;

public abstract class CharacterState implements State<Entity> {

    protected final ComponentMapper<PlayerComponent> players;
    protected final ComponentMapper<BodyComponent> bodies;
    protected final ComponentMapper<AnimatedComponent> animated;
    protected final ComponentMapper<InputComponent> inputs;

    private float delta;

    public CharacterState(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs) {
        this.players = players;
        this.bodies = bodies;
        this.animated = animated;
        this.inputs = inputs;
    }

    protected abstract String animation();

    public final void setDelta(float delta) {
        this.delta = delta;
    }

    protected final float delta() {
        return delta;
    }

    @Override
    public void enter(Entity entity) {
        animated.get(entity).stateMachine.enter(animation());
    }

    @Override
    public void exit(Entity entity) {}

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        if (telegram.message == Message.PLAYER_ON_SPIKE.ordinal()) {
            PlayerComponent playerComponent = players.get(entity);
            playerComponent.stateMachine.changeState(playerComponent.dead);
            return true;
        }
        return false;
    }
}
