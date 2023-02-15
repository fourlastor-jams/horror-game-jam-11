package io.github.fourlastor.game.level.unphysics.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import javax.inject.Inject;

public class GravitySystem extends IntervalSystem {

    private static final float INTERVAL = 1f / 60f;
    private static final Family FAMILY = Family.all(
                    KinematicBodyComponent.class, MovingBodyComponent.class, GravityComponent.class)
            .get();

    private final ComponentMapper<MovingBodyComponent> movingBodies;
    private final ComponentMapper<GravityComponent> gravities;
    private ImmutableArray<Entity> entities;

    @Inject
    public GravitySystem(
            ComponentMapper<MovingBodyComponent> movingBodies, ComponentMapper<GravityComponent> gravities) {
        super(INTERVAL);
        this.movingBodies = movingBodies;
        this.gravities = gravities;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(FAMILY);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
        super.removedFromEngine(engine);
    }

    @Override
    protected void updateInterval() {
        for (Entity entity : entities) {
            MovingBodyComponent movingBody = movingBodies.get(entity);
            Vector2 gravity = gravities.get(entity).gravity;
            movingBody.speed.add(gravity.x * INTERVAL, gravity.y * INTERVAL);
        }
    }
}
