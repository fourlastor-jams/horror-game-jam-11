package io.github.fourlastor.game.level.entity.timedFloor;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.state.Degraded;
import io.github.fourlastor.game.level.entity.timedFloor.state.Degrading;
import io.github.fourlastor.game.level.entity.timedFloor.state.Intact;
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
    private static final Family FAMILY_REMOVAL =
            Family.all(TimedFloorComponent.Removal.class).get();
    private final ComponentMapper<TimedFloorComponent> falseFloors;
    private final MessageDispatcher messageDispatcher;
    private final SetupListener setupListener;
    private RemovalListener removalListener;

    @Inject
    public TimedFloorSystem(
            ComponentMapper<TimedFloorComponent> falseFloors,
            MessageDispatcher messageDispatcher,
            SetupListener setupListener) {
        super(FAMILY);
        this.falseFloors = falseFloors;
        this.messageDispatcher = messageDispatcher;
        this.setupListener = setupListener;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        falseFloors.get(entity).stateMachine.update(deltaTime);
    }

    @Override
    public void addedToEngine(Engine engine) {
        removalListener = new RemovalListener(engine, messageDispatcher);
        engine.addEntityListener(FAMILY_SETUP, setupListener);
        engine.addEntityListener(FAMILY_REMOVAL, removalListener);
        super.addedToEngine(engine);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(setupListener);
        engine.removeEntityListener(removalListener);
        removalListener = null;
        super.removedFromEngine(engine);
    }

    public static class SetupListener implements EntityListener {

        private final Provider<Intact> intactFactory;
        private final Provider<Degrading> degradingFactory;
        private final Provider<Degraded> degradedFactory;
        private final TimedFloorStateMachine.Factory stateMachineFactory;
        private final MessageDispatcher messageDispatcher;

        @Inject
        public SetupListener(
                Provider<Intact> intactFactory,
                Provider<Degrading> degradingFactory,
                Provider<Degraded> degradedFactory,
                TimedFloorStateMachine.Factory stateMachineFactory,
                MessageDispatcher messageDispatcher) {
            this.intactFactory = intactFactory;
            this.degradingFactory = degradingFactory;
            this.degradedFactory = degradedFactory;
            this.stateMachineFactory = stateMachineFactory;
            this.messageDispatcher = messageDispatcher;
        }

        @Override
        public void entityAdded(Entity entity) {
            entity.remove(TimedFloorComponent.Request.class);
            Intact intact = intactFactory.get();
            Degrading degrading = degradingFactory.get();
            Degraded degraded = degradedFactory.get();
            TimedFloorStateMachine stateMachine = stateMachineFactory.create(entity, intact);
            entity.add(new TimedFloorComponent(stateMachine, intact, degrading, degraded));
            stateMachine.getCurrentState().enter(entity);
            messageDispatcher.addListener(stateMachine, Message.PLAYER_ON_GROUND.ordinal());
        }

        @Override
        public void entityRemoved(Entity entity) {}
    }

    public static class RemovalListener implements EntityListener {

        private final Engine engine;
        private final MessageDispatcher messageDispatcher;

        public RemovalListener(Engine engine, MessageDispatcher messageDispatcher) {
            this.engine = engine;
            this.messageDispatcher = messageDispatcher;
        }

        @Override
        public void entityAdded(Entity entity) {
            TimedFloorComponent timedFloorComponent = entity.remove(TimedFloorComponent.class);
            messageDispatcher.removeListener(timedFloorComponent.stateMachine);
            engine.removeEntity(entity);
        }

        @Override
        public void entityRemoved(Entity entity) {}
    }
}
