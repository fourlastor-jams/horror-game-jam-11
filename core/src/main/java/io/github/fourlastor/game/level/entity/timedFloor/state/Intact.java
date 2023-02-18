package io.github.fourlastor.game.level.entity.timedFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import javax.inject.Inject;

public class Intact extends TimedFloorState {

    @Inject
    public Intact(
            ComponentMapper<SolidBodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<TimedFloorComponent> inputs) {
        super(bodies, actors, inputs);
    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        if (telegram.message == Message.PLAYER_ON_GROUND.ordinal() && telegram.extraInfo == entity) {
            TimedFloorComponent timedFloorComponent = falseFloors.get(entity);
            timedFloorComponent.stateMachine.changeState(timedFloorComponent.degrading);
        }
        return super.onMessage(entity, telegram);
    }
}
