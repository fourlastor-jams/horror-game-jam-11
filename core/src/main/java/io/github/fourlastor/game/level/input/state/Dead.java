package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;
import javax.inject.Inject;

public class Dead extends CharacterState {

    @Inject
    public Dead(StateMappers mappers) {
        super(mappers);
    }

    @Override
    protected String animation() {
        return "dead";
    }

    @Override
    public void update(Entity entity) {}

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
