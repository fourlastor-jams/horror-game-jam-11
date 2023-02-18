package io.github.fourlastor.game.level.entity.timedFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;

public abstract class TimedFloorState implements State<Entity> {

    protected final ComponentMapper<SolidBodyComponent> bodies;
    protected final ComponentMapper<ActorComponent> actors;
    protected final ComponentMapper<TimedFloorComponent> timedFloors;

    private float delta;
    private float timer;

    public TimedFloorState(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<TimedFloorComponent> timedFloors) {
        this.bodies = bodies;
        this.actors = actors;
        this.timedFloors = timedFloors;
    }

    public final void setDelta(float delta) {
        this.delta = delta;
    }

    protected final float delta() {
        return delta;
    }

    @Override
    public void enter(Entity entity) {
        timer = 0f;
        actors.get(entity).actor.setColor(color());
        bodies.get(entity).canCollide = canCollide();
    }

    @Override
    public void update(Entity entity) {
        timer += delta();
        TimedFloorComponent timedFloor = timedFloors.get(entity);
        if (timer >= timedFloor.period) {
            timedFloor.stateMachine.changeState(nextState(timedFloor));
        }
    }

    protected abstract boolean canCollide();

    protected abstract Color color();

    protected abstract TimedFloorState nextState(TimedFloorComponent timedFloor);

    @Override
    public void exit(Entity entity) {}

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
