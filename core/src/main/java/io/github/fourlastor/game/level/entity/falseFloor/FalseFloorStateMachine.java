package io.github.fourlastor.game.level.entity.falseFloor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.level.entity.falseFloor.state.FalseFloorState;

public class FalseFloorStateMachine extends DefaultStateMachine<Entity, FalseFloorState> {

    @AssistedInject
    public FalseFloorStateMachine(@Assisted Entity entity, @Assisted FalseFloorState initialState) {
        super(entity, initialState);
    }

    public void update(float deltaTime) {
        currentState.setDelta(deltaTime);
        update();
    }

    @AssistedFactory
    public interface Factory {
        FalseFloorStateMachine create(Entity entity, FalseFloorState initialState);
    }
}
