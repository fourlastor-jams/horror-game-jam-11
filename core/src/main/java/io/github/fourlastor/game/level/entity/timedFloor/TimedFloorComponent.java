package io.github.fourlastor.game.level.entity.timedFloor;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.entity.timedFloor.state.Disabled;
import io.github.fourlastor.game.level.entity.timedFloor.state.Enabled;

public class TimedFloorComponent implements Component {

    public final TimedFloorStateMachine stateMachine;
    public final Enabled enabled;
    public final Disabled disabled;

    public final float period;

    public TimedFloorComponent(TimedFloorStateMachine stateMachine, Enabled enabled, Disabled disabled, float period) {
        this.stateMachine = stateMachine;
        this.enabled = enabled;
        this.disabled = disabled;
        this.period = period;
    }

    public static class Request implements Component {
        public final boolean enabled;
        public final float period;

        public Request(boolean enabled, float period) {
            this.enabled = enabled;
            this.period = period;
        }
    }
}
