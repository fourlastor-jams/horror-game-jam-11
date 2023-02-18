package io.github.fourlastor.game.level.entity.timedFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;

public abstract class TimedFloorState implements State<Entity> {

    protected final ComponentMapper<SolidBodyComponent> bodies;
    protected final ComponentMapper<ActorComponent> actors;
    protected final ComponentMapper<TimedFloorComponent> falseFloors;

    private float delta;

    public TimedFloorState(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<TimedFloorComponent> falseFloors) {
        this.bodies = bodies;
        this.actors = actors;
        this.falseFloors = falseFloors;
    }

    public final void setDelta(float delta) {
        this.delta = delta;
    }

    protected final float delta() {
        return delta;
    }

    @Override
    public void update(Entity entity) {}

    @Override
    public void enter(Entity entity) {}

    @Override
    public void exit(Entity entity) {}

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
