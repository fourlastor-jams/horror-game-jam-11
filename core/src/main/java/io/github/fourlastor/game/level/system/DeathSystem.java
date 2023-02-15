package io.github.fourlastor.game.level.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.component.SpikeComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;

import javax.inject.Inject;

public class DeathSystem extends IteratingSystem {

    private static final Family FAMILY = Family.all(
                    KinematicBodyComponent.class, PlayerComponent.class)
            .get();
    private final ComponentMapper<KinematicBodyComponent> bodies;
    private final ComponentMapper<SpikeComponent> spikes;
    private final MessageDispatcher messageDispatcher;

    @Inject
    public DeathSystem(ComponentMapper<KinematicBodyComponent> bodies, ComponentMapper<SpikeComponent> spikes, MessageDispatcher messageDispatcher) {
        super(FAMILY);
        this.bodies = bodies;
        this.spikes = spikes;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        for (Entity sensor : bodies.get(entity).sensors) {
            if (spikes.has(sensor)) {
                messageDispatcher.dispatchMessage(Message.PLAYER_ON_SPIKE.ordinal());
            }
        }
    }
}
