package io.github.fourlastor.game.level.unphysics.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.FollowBodyComponent;
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

/**
 * Coordinates the movement between each pair of scene2d actor and box2d body.
 * Actors follow the bodies.
 */
public class ActorFollowTransformSystem extends IteratingSystem {

    private static final Family FAMILY = Family.all(
                    ActorComponent.class, TransformComponent.class, FollowBodyComponent.class)
            .get();
    private final ComponentMapper<TransformComponent> transforms;
    private final ComponentMapper<ActorComponent> actors;

    @Inject
    public ActorFollowTransformSystem(
            ComponentMapper<TransformComponent> transforms, ComponentMapper<ActorComponent> actors) {
        super(FAMILY);
        this.transforms = transforms;
        this.actors = actors;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Actor actor = actors.get(entity).actor;
        Transform transform = transforms.get(entity).transform;
        actor.setPosition(transform.left(), transform.top());
    }
}
