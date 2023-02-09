package io.github.fourlastor.game.level.entity.falseFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.entity.falseFloor.FalseFloorComponent;
import javax.inject.Inject;

public class Degrading extends FalseFloorState {

    private float timer = 0f;

    @Inject
    public Degrading(
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<FalseFloorComponent> inputs) {
        super(bodies, actors, inputs);
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        actors.get(entity).actor.setColor(Color.BLUE);
        timer = 0f;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        timer += delta();
        if (timer > 1f) {
            FalseFloorComponent falseFloorComponent = falseFloors.get(entity);
            falseFloorComponent.stateMachine.changeState(falseFloorComponent.degraded);
        }
    }
}
