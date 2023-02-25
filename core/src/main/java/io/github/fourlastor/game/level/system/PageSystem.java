package io.github.fourlastor.game.level.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.fourlastor.game.level.Area;
import io.github.fourlastor.game.level.component.AreaComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import javax.inject.Inject;

public class PageSystem extends IteratingSystem {

    private static final Family FAMILY =
            Family.all(PlayerComponent.class, KinematicBodyComponent.class).get();

    private final ComponentMapper<PlayerComponent> players;
    private final ComponentMapper<KinematicBodyComponent> bodies;
    private final ComponentMapper<AreaComponent> areas;

    @Inject
    public PageSystem(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<KinematicBodyComponent> bodies,
            ComponentMapper<AreaComponent> areas) {
        super(FAMILY);
        this.players = players;
        this.bodies = bodies;
        this.areas = areas;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        for (Entity sensor : bodies.get(entity).sensors) {
            AreaComponent area = areas.get(sensor);
            if (area != null && area.area == Area.PAGE) {
                getEngine().removeEntity(sensor);
                PlayerComponent playerComponent = players.get(entity);
                playerComponent.pages += 1;
            }
        }
    }
}
