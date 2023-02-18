package io.github.fourlastor.game.level.entity.timedFloor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.github.fourlastor.game.level.entity.timedFloor.state.TimedFloorState;

public class TimedFloorStateMachine extends DefaultStateMachine<Entity, TimedFloorState> {

    @AssistedInject
    public TimedFloorStateMachine(@Assisted Entity entity, @Assisted TimedFloorState initialState) {
        super(entity, initialState);
    }

    public void update(float deltaTime) {
        currentState.setDelta(deltaTime);
        update();
    }

    @AssistedFactory
    public interface Factory {
        TimedFloorStateMachine create(Entity entity, TimedFloorState initialState);
    }
}
