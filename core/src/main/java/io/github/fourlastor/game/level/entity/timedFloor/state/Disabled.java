package io.github.fourlastor.game.level.entity.timedFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import javax.inject.Inject;

public class Disabled extends TimedFloorState {

    @Inject
    public Disabled(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<TimedFloorComponent> timedFloors) {
        super(bodies, actors, timedFloors);
    }

    @Override
    protected boolean canCollide() {
        return false;
    }

    @Override
    protected Color color() {
        return Color.BLUE;
    }

    @Override
    protected TimedFloorState nextState(TimedFloorComponent timedFloor) {
        return timedFloor.enabled;
    }
}
