package io.github.fourlastor.game.level.entity.falseFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.falseFloor.FalseFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import javax.inject.Inject;

public class Degraded extends FalseFloorState {

    @Inject
    public Degraded(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<FalseFloorComponent> inputs) {
        super(bodies, actors, inputs);
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        entity.add(new FalseFloorComponent.Removal());
    }
}
