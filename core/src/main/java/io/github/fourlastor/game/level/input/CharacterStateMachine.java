package io.github.fourlastor.game.level.input;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.level.input.state.CharacterState;

public class CharacterStateMachine extends DefaultStateMachine<Entity, CharacterState> {

    @AssistedInject
    public CharacterStateMachine(@Assisted Entity entity, @Assisted CharacterState initialState) {
        super(entity, initialState);
    }

    public void update(float deltaTime) {
        currentState.setDelta(deltaTime);
        update();
    }

    @AssistedFactory
    public interface Factory {
        CharacterStateMachine create(Entity entity, CharacterState initialState);
    }
}
