package io.github.fourlastor.game.level.unphysics.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SensorBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * @see <a href=https://maddythorson.medium.com/celeste-and-towerfall-physics-d24bd2ae0fc5>Tutorial by Maddy Thorson</a>
 */
public class TransformDebugSystem extends IteratingSystem {

    private static final Family FAMILY = Family.all(TransformComponent.class).get();

    private final ComponentMapper<TransformComponent> transforms;
    private final ComponentMapper<SolidBodyComponent> solidBodies;
    private final ComponentMapper<KinematicBodyComponent> kinematicBodies;
    private final ComponentMapper<SensorBodyComponent> sensorBodies;
    private final ShapeDrawer drawer;
    private final Batch batch;

    @Inject
    public TransformDebugSystem(
            ComponentMapper<TransformComponent> transforms,
            ComponentMapper<SolidBodyComponent> solidBodies,
            ComponentMapper<KinematicBodyComponent> kinematicBodies,
            ComponentMapper<SensorBodyComponent> sensorBodies,
            Stage stage,
            TextureAtlas atlas) {
        super(FAMILY);
        this.transforms = transforms;
        this.solidBodies = solidBodies;
        this.kinematicBodies = kinematicBodies;
        this.sensorBodies = sensorBodies;
        batch = stage.getBatch();
        drawer = new ShapeDrawer(batch, atlas.findRegion("whitePixel"));
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = transforms.get(entity).transform;
        if (kinematicBodies.has(entity)) {
            drawer.setColor(Color.PINK);
        } else if (solidBodies.has(entity)) {
            drawer.setColor(Color.CYAN);
        } else if (sensorBodies.has(entity)) {
            drawer.setColor(Color.GOLD);
        } else {
            drawer.setColor(Color.WHITE);
        }
        drawer.rectangle(transform.area());
    }
}
