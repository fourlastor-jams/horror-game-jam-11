package io.github.fourlastor.game.level.entity.timedFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import javax.inject.Inject;

public class Degraded extends TimedFloorState {

    @Inject
    public Degraded(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<TimedFloorComponent> inputs) {
        super(bodies, actors, inputs);
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        entity.add(new TimedFloorComponent.Removal());
    }
}
