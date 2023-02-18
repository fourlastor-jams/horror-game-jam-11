package io.github.fourlastor.game.level.entity.timedFloor;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.state.Disabled;
import io.github.fourlastor.game.level.entity.timedFloor.state.Enabled;
import io.github.fourlastor.game.level.entity.timedFloor.state.TimedFloorState;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Coordinates the movement between each pair of scene2d actor and box2d body.
 * Actors follow the bodies.
 */
public class TimedFloorSystem extends IteratingSystem {

    private static final Family FAMILY = Family.all(
                    SolidBodyComponent.class, ActorComponent.class, TimedFloorComponent.class)
            .get();
    private static final Family FAMILY_SETUP =
            Family.all(TimedFloorComponent.Request.class).get();
    private final ComponentMapper<TimedFloorComponent> falseFloors;
    private final SetupListener setupListener;

    @Inject
    public TimedFloorSystem(ComponentMapper<TimedFloorComponent> falseFloors, SetupListener setupListener) {
        super(FAMILY);
        this.falseFloors = falseFloors;
        this.setupListener = setupListener;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        falseFloors.get(entity).stateMachine.update(deltaTime);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(FAMILY_SETUP, setupListener);
        super.addedToEngine(engine);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(setupListener);
        super.removedFromEngine(engine);
    }

    public static class SetupListener implements EntityListener {

        private final Provider<Enabled> enabledFactory;
        private final Provider<Disabled> disabledFactory;
        private final TimedFloorStateMachine.Factory stateMachineFactory;

        @Inject
        public SetupListener(
                Provider<Enabled> enabledFactory,
                Provider<Disabled> disabledFactory,
                TimedFloorStateMachine.Factory stateMachineFactory) {
            this.enabledFactory = enabledFactory;
            this.disabledFactory = disabledFactory;
            this.stateMachineFactory = stateMachineFactory;
        }

        @Override
        public void entityAdded(Entity entity) {
            TimedFloorComponent.Request request = entity.remove(TimedFloorComponent.Request.class);
            Enabled enabled = enabledFactory.get();
            Disabled disabled = disabledFactory.get();
            TimedFloorState initialState = request.enabled ? enabled : disabled;
            TimedFloorStateMachine stateMachine = stateMachineFactory.create(entity, initialState);
            entity.add(new TimedFloorComponent(stateMachine, enabled, disabled, request.period));
            stateMachine.getCurrentState().enter(entity);
        }

        @Override
        public void entityRemoved(Entity entity) {}
    }
}
