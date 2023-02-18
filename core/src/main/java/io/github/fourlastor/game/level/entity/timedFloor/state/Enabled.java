package io.github.fourlastor.game.level.entity.timedFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import javax.inject.Inject;

public class Enabled extends TimedFloorState {

    @Inject
    public Enabled(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<TimedFloorComponent> timedFloors) {
        super(bodies, actors, timedFloors);
    }

    @Override
    protected boolean canCollide() {
        return true;
    }

    @Override
    protected Color color() {
        return Color.WHITE;
    }

    @Override
    protected TimedFloorState nextState(TimedFloorComponent timedFloor) {
        return timedFloor.disabled;
    }
}
