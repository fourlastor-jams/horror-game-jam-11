package io.github.fourlastor.game.level.entity.falseFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.entity.falseFloor.FalseFloorComponent;

public abstract class FalseFloorState implements State<Entity> {

    protected final ComponentMapper<BodyComponent> bodies;
    protected final ComponentMapper<ActorComponent> actors;
    protected final ComponentMapper<FalseFloorComponent> falseFloors;

    private float delta;

    public FalseFloorState(
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<FalseFloorComponent> falseFloors) {
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
