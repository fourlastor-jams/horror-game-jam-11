package io.github.fourlastor.game.level.input;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.input.controls.Control;
import io.github.fourlastor.game.level.input.controls.Controls;
import javax.inject.Inject;

public class InputBufferSystem extends IteratingSystem {

    private static final Family FAMILY =
            Family.all(PlayerComponent.class, InputComponent.class).get();

    private final ComponentMapper<PlayerComponent> players;
    private final ComponentMapper<InputComponent> inputs;

    @Inject
    public InputBufferSystem(ComponentMapper<PlayerComponent> players, ComponentMapper<InputComponent> inputs) {
        super(FAMILY);
        this.players = players;
        this.inputs = inputs;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Controls controls = players.get(entity).controls;
        Control jump = controls.jump();
        Control left = controls.left();
        Control right = controls.right();
        InputComponent input = inputs.get(entity);
        boolean jumpPressed = jump.pressed();
        boolean leftPressed = left.pressed();
        boolean rightPressed = right.pressed();
        input.jumpJustPressed = jumpPressed && !input.jumpPressed;
        input.jumpPressed = jumpPressed;
        boolean leftJustPressed = leftPressed && !input.leftPressed;
        boolean rightJustPressed = rightPressed && !input.rightPressed;
        boolean leftJustReleased = !leftPressed && input.leftPressed;
        boolean rightJustReleased = !rightPressed && input.rightPressed;
        boolean movementReleased = !leftPressed && !rightPressed && (leftJustReleased || rightJustReleased);
        input.movementChanged = leftJustPressed || rightJustPressed || movementReleased;
        input.leftPressed = leftPressed;
        input.rightPressed = rightPressed;
    }
}
