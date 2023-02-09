package io.github.fourlastor.game.level.entity.falseFloor;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.entity.falseFloor.state.Degraded;
import io.github.fourlastor.game.level.entity.falseFloor.state.Degrading;
import io.github.fourlastor.game.level.entity.falseFloor.state.Intact;

public class FalseFloorComponent implements Component {

    public final FalseFloorStateMachine stateMachine;
    public final Intact intact;
    public final Degrading degrading;
    public final Degraded degraded;

    public FalseFloorComponent(
            FalseFloorStateMachine stateMachine, Intact intact, Degrading degrading, Degraded degraded) {
        this.stateMachine = stateMachine;
        this.intact = intact;
        this.degrading = degrading;
        this.degraded = degraded;
    }

    public static class Request implements Component {}

    public static class Removal implements Component {}
}
