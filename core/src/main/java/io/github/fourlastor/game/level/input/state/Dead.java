package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

public class Dead extends CharacterState {

    @Inject
    public Dead(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<KinematicBodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs,
            ComponentMapper<MovingBodyComponent> moving,
            ComponentMapper<TransformComponent> transforms) {
        super(players, bodies, moving, transforms, animated, inputs);
    }

    @Override
    protected String animation() {
        return "dead";
    }

    @Override
    public void update(Entity entity) {}

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
