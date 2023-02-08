package io.github.fourlastor.game.level.component;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.input.controls.Command;

public class InputComponent implements Component {

    public Command command = Command.IDLE;
}
