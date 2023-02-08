package io.github.fourlastor.game.level.component;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.harlequin.ui.AnimationStateMachine;

public class AnimatedComponent implements Component {

    public final AnimationStateMachine stateMachine;

    public AnimatedComponent(AnimationStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
}
