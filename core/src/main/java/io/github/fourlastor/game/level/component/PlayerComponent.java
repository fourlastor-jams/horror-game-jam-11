package io.github.fourlastor.game.level.component;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.input.CharacterStateMachine;
import io.github.fourlastor.game.level.input.controls.Controls;
import io.github.fourlastor.game.level.input.state.FallingFromGround;
import io.github.fourlastor.game.level.input.state.FallingFromJump;
import io.github.fourlastor.game.level.input.state.Idle;
import io.github.fourlastor.game.level.input.state.Jumping;
import io.github.fourlastor.game.level.input.state.Running;

/**
 * Bag containing the player state machine, and the possible states it can get into.
 */
public class PlayerComponent implements Component {

    public final Controls controls;
    public final CharacterStateMachine stateMachine;
    public final Idle idle;
    public final Running running;
    public final Jumping jumping;
    public final FallingFromJump fallingFromJump;
    public final FallingFromGround fallingFromGround;

    public PlayerComponent(
            Controls controls,
            CharacterStateMachine stateMachine,
            Idle idle,
            Running running,
            Jumping jumping,
            FallingFromJump fallingFromJump,
            FallingFromGround fallingFromGround) {
        this.controls = controls;
        this.stateMachine = stateMachine;
        this.idle = idle;
        this.running = running;
        this.jumping = jumping;
        this.fallingFromJump = fallingFromJump;
        this.fallingFromGround = fallingFromGround;
    }
}
