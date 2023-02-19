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

public class AreaSystem extends IteratingSystem {

    private static final Family FAMILY =
            Family.all(KinematicBodyComponent.class, PlayerComponent.class).get();
    private final ComponentMapper<KinematicBodyComponent> bodies;
    private final ComponentMapper<AreaComponent> areas;
    private final ComponentMapper<PlayerComponent> players;

    @Inject
    public AreaSystem(
            ComponentMapper<KinematicBodyComponent> bodies,
            ComponentMapper<AreaComponent> areas,
            ComponentMapper<PlayerComponent> players) {
        super(FAMILY);
        this.bodies = bodies;
        this.areas = areas;
        this.players = players;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = players.get(entity);
        player.area = Area.NONE;
        for (Entity sensor : bodies.get(entity).sensors) {
            if (areas.has(sensor)) {
                player.area = areas.get(sensor).area;
                return;
            }
        }
    }
}
