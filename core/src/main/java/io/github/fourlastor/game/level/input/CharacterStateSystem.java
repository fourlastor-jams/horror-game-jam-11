package io.github.fourlastor.game.level.input;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.component.PlayerRequestComponent;
import io.github.fourlastor.game.level.input.controls.Controls;
import io.github.fourlastor.game.level.input.state.FallingFromGround;
import io.github.fourlastor.game.level.input.state.FallingFromJump;
import io.github.fourlastor.game.level.input.state.Idle;
import io.github.fourlastor.game.level.input.state.Jumping;
import io.github.fourlastor.game.level.input.state.Running;

import javax.inject.Inject;
import javax.inject.Provider;

public class CharacterStateSystem extends IteratingSystem {

    private static final Family FAMILY_REQUEST =
            Family.all(PlayerRequestComponent.class, BodyComponent.class).get();
    private static final Family FAMILY = Family.all(
                    PlayerComponent.class,
                    BodyComponent.class,
                    ActorComponent.class,
                    InputComponent.class,
                    AnimatedComponent.class)
            .get();

    private final PlayerSetup playerSetup;
    private final ComponentMapper<PlayerComponent> players;

    @Inject
    public CharacterStateSystem(PlayerSetup playerSetup, ComponentMapper<PlayerComponent> players) {
        super(FAMILY);
        this.playerSetup = playerSetup;
        this.players = players;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        players.get(entity).stateMachine.update(deltaTime);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(FAMILY_REQUEST, playerSetup);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(playerSetup);
        super.removedFromEngine(engine);
    }

    /**
     * Creates a player component whenever a request to set up a player is made.
     * Takes care of instantiating the state machine and the possible player states.
     */
    public static class PlayerSetup implements EntityListener {

        private final Provider<Idle> idleFactory;
        private final Provider<Running> walkingFactory;
        private final Provider<Jumping> jumpingFactory;
        private final Provider<FallingFromJump> fallingFromJumpFactory;
        private final Provider<FallingFromGround> fallingFromGroundFactory;
        private final CharacterStateMachine.Factory stateMachineFactory;
        private final MessageDispatcher messageDispatcher;

        @Inject
        public PlayerSetup(
                Provider<Idle> idleFactory,
                Provider<Running> walkingFactory,
                Provider<Jumping> jumpingFactory,
                Provider<FallingFromJump> fallingFromJumpFactory,
                Provider<FallingFromGround> fallingFromGroundFactory, CharacterStateMachine.Factory stateMachineFactory,
                MessageDispatcher messageDispatcher) {
            this.idleFactory = idleFactory;
            this.walkingFactory = walkingFactory;
            this.jumpingFactory = jumpingFactory;
            this.fallingFromJumpFactory = fallingFromJumpFactory;
            this.fallingFromGroundFactory = fallingFromGroundFactory;
            this.stateMachineFactory = stateMachineFactory;
            this.messageDispatcher = messageDispatcher;
        }

        @Override
        public void entityAdded(Entity entity) {
            PlayerRequestComponent request = entity.remove(PlayerRequestComponent.class);

            Controls controls = request.controls;
            Idle idle = idleFactory.get();
            Running running = walkingFactory.get();
            Jumping jumping = jumpingFactory.get();
            FallingFromJump fallingFromJump = fallingFromJumpFactory.get();
            CharacterStateMachine stateMachine = stateMachineFactory.create(entity, idle);
            FallingFromGround fallingFromGround = fallingFromGroundFactory.get();
            entity.add(new PlayerComponent(controls, stateMachine, idle, running, jumping, fallingFromJump, fallingFromGround));
            stateMachine.getCurrentState().enter(entity);
            for (Message value : Message.values()) {
                messageDispatcher.addListener(stateMachine, value.ordinal());
            }
        }

        @Override
        public void entityRemoved(Entity entity) {}
    }
}
