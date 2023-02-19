package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.Area;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;

public abstract class CharacterState implements State<Entity> {

    protected final ComponentMapper<PlayerComponent> players;
    protected final ComponentMapper<KinematicBodyComponent> bodies;
    protected final ComponentMapper<MovingBodyComponent> moving;
    protected final ComponentMapper<TransformComponent> transforms;
    protected final ComponentMapper<AnimatedComponent> animated;
    protected final ComponentMapper<InputComponent> inputs;

    private float delta;

    public CharacterState(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<KinematicBodyComponent> bodies,
            ComponentMapper<MovingBodyComponent> moving,
            ComponentMapper<TransformComponent> transforms,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs) {
        this.players = players;
        this.bodies = bodies;
        this.moving = moving;
        this.transforms = transforms;
        this.animated = animated;
        this.inputs = inputs;
    }

    protected abstract String animation();

    public final void setDelta(float delta) {
        this.delta = delta;
    }

    protected final float delta() {
        return delta;
    }

    @Override
    public void enter(Entity entity) {
        animated.get(entity).stateMachine.enter(animation());
    }

    @Override
    public void exit(Entity entity) {}

    @Override
    public void update(Entity entity) {
        PlayerComponent player = players.get(entity);
        if (player.area == Area.SPIKES) {
            player.stateMachine.changeState(player.dead);
        }
    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
