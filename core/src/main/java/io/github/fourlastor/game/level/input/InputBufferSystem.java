package io.github.fourlastor.game.level.input;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.input.controls.Control;
import io.github.fourlastor.game.level.input.controls.Controls;
import javax.inject.Inject;

public class InputBufferSystem extends IntervalSystem {

    private static final Family FAMILY =
            Family.all(PlayerComponent.class, InputComponent.class).get();

    private final ComponentMapper<PlayerComponent> players;
    private final ComponentMapper<InputComponent> inputs;
    private ImmutableArray<Entity> entities;

    @Inject
    public InputBufferSystem(ComponentMapper<PlayerComponent> players, ComponentMapper<InputComponent> inputs) {
        super(1f / 60f);
        this.players = players;
        this.inputs = inputs;
    }

    @Override
    protected void updateInterval() {
        for (Entity entity : entities) {
            Controls controls = players.get(entity).controls;
            Control jump = controls.jump();
            Control left = controls.left();
            Control right = controls.right();
            InputComponent input = inputs.get(entity);
            input.jumpJustPressed = jump.pressed() && !input.jumpPressed;
            input.jumpPressed = jump.pressed();
            boolean leftJustPressed = left.pressed() && !input.leftPressed;
            boolean rightJustPressed = right.pressed() && !input.rightPressed;
            boolean leftJustReleased = !left.pressed() && input.leftPressed;
            boolean rightJustReleased = !right.pressed() && input.rightPressed;
            boolean movementReleased = !left.pressed() && !right.pressed() && (leftJustReleased || rightJustReleased);
            input.movementChanged = leftJustPressed || rightJustPressed || movementReleased;
            input.leftPressed = controls.left().pressed();
            input.rightPressed = controls.right().pressed();
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(FAMILY);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        entities = null;
    }
}
