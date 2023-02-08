package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;

abstract class Falling extends HorizontalMovement {

    protected final GameConfig config;

    private float fallingTime = 0f;
    private float attemptedTime = -1;


    public Falling(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs, GameConfig config) {
        super(players, bodies, animated, inputs, config);
        this.config = config;
    }

    protected final float fallingTime() {
        return fallingTime;
    }

    @Override
    protected String animation() {
        return "fall";
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        fallingTime = 0f;
        attemptedTime = -1f;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        fallingTime += delta();
        if (inputs.get(entity).jumpJustPressed) {
            attemptedTime = fallingTime;
        }
    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        if (telegram.message == Message.PLAYER_ON_GROUND.ordinal()) {
            PlayerComponent playerComponent = players.get(entity);
            if (attemptedTime >= 0 && fallingTime - attemptedTime < config.player.fallingGraceTime) {
                playerComponent.stateMachine.changeState(playerComponent.jumping);
            } else {
                playerComponent.stateMachine.changeState(playerComponent.idle);
            }
            return true;
        }
        return super.onMessage(entity, telegram);
    }
}
