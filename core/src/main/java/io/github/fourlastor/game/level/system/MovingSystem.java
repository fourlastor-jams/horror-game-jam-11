package io.github.fourlastor.game.level.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.github.fourlastor.game.level.component.MovingComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;

import java.util.List;
import javax.inject.Inject;

public class MovingSystem extends IteratingSystem {
    private static final Family FAMILY =
            Family.all(MovingComponent.class, MovingBodyComponent.class, TransformComponent.class).get();

    private final ComponentMapper<MovingBodyComponent> bodies;
    private final ComponentMapper<TransformComponent> transforms;
    private final ComponentMapper<MovingComponent> movables;
    private final Vector2 position = new Vector2();
    private final Vector2 velocity = new Vector2();

    @Inject
    public MovingSystem(ComponentMapper<MovingBodyComponent> bodies, ComponentMapper<TransformComponent> transforms, ComponentMapper<MovingComponent> movables) {
        super(FAMILY);
        this.bodies = bodies;
        this.transforms = transforms;
        this.movables = movables;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovingComponent movingComponent = movables.get(entity);
        Vector2 position = transforms.get(entity).transform.area().getPosition(this.position);
        List<Vector2> path = movingComponent.path;
        int current = movingComponent.position;
        int next = (current + 1) % path.size();
        Vector2 destination = path.get(next);
        float dst = position.dst(destination);
        if (dst > 1f) {
            velocity.set(destination).sub(position).nor().scl(movingComponent.speed);
            bodies.get(entity).speed.set(velocity);
        } else {
            movingComponent.position = next;
        }
    }
}
