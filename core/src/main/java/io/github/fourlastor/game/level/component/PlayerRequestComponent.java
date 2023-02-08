package io.github.fourlastor.game.level.component;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.input.controls.Controls;

/** Request to create a Player. */
public class PlayerRequestComponent implements Component {
    public final Controls controls;

    public PlayerRequestComponent(Controls controls) {
        this.controls = controls;
    }
}
