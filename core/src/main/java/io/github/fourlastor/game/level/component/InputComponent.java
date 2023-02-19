package io.github.fourlastor.game.level.component;

import com.badlogic.ashley.core.Component;

public class InputComponent implements Component {

    public boolean leftPressed;
    public boolean rightPressed;
    public boolean movementChanged;
    public boolean jumpPressed;
    public boolean jumpJustPressed;
    public boolean upPressed;
    public boolean downPressed;
}
