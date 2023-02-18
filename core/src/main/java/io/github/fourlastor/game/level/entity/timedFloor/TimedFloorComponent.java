package io.github.fourlastor.game.level.entity.timedFloor;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.entity.timedFloor.state.Degraded;
import io.github.fourlastor.game.level.entity.timedFloor.state.Degrading;
import io.github.fourlastor.game.level.entity.timedFloor.state.Intact;

public class TimedFloorComponent implements Component {

    public final TimedFloorStateMachine stateMachine;
    public final Intact intact;
    public final Degrading degrading;
    public final Degraded degraded;

    public TimedFloorComponent(
            TimedFloorStateMachine stateMachine, Intact intact, Degrading degrading, Degraded degraded) {
        this.stateMachine = stateMachine;
        this.intact = intact;
        this.degrading = degrading;
        this.degraded = degraded;
    }

    public static class Request implements Component {}

    public static class Removal implements Component {}
}
