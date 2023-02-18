package io.github.fourlastor.game.level.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

/**
 * Moves platforms down when the player goes up
 */
public class CameraMovementSystem extends IteratingSystem {

    private static final Family FAMILY_PLAYER =
            Family.all(PlayerComponent.class, TransformComponent.class).get();
    private final Camera camera;
    private final ComponentMapper<TransformComponent> transforms;

    @Inject
    public CameraMovementSystem(Camera camera, ComponentMapper<TransformComponent> transforms) {
        super(FAMILY_PLAYER);
        this.camera = camera;
        this.transforms = transforms;
    }

    private final Vector2 center = new Vector2();
    private final Vector2 cameraPos = new Vector2();

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        transforms.get(entity).transform.area().getCenter(center);
        cameraPos.set(camera.position.x, camera.position.y);
        center.sub(cameraPos).scl(deltaTime * 8);
        camera.position.x += center.x;
        camera.position.y += center.y;
    }
}
