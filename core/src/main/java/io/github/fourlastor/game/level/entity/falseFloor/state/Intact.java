package io.github.fourlastor.game.level.entity.falseFloor.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;
import io.github.fourlastor.game.level.Message;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.entity.falseFloor.FalseFloorComponent;
import javax.inject.Inject;

public class Intact extends FalseFloorState {

    @Inject
    public Intact(
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<ActorComponent> actors,
            ComponentMapper<FalseFloorComponent> inputs) {
        super(bodies, actors, inputs);
    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        if (telegram.message == Message.PLAYER_ON_GROUND.ordinal() && telegram.extraInfo == entity) {
            FalseFloorComponent falseFloorComponent = falseFloors.get(entity);
            falseFloorComponent.stateMachine.changeState(falseFloorComponent.degrading);
        }
        return super.onMessage(entity, telegram);
    }
}
