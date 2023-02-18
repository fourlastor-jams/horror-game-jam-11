package io.github.fourlastor.game.level.entity.timedFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import javax.inject.Inject;

public class Degrading extends TimedFloorState {

    private float timer = 0f;

    @Inject
    public Degrading(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<TimedFloorComponent> inputs) {
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
            TimedFloorComponent timedFloorComponent = falseFloors.get(entity);
            timedFloorComponent.stateMachine.changeState(timedFloorComponent.degraded);
        }
    }
}
