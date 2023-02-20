package io.github.fourlastor.game.level.unphysics.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovingBodyComponent implements Component {
    public final Vector2 speed = new Vector2();
    public float xRemainder = 0f;
    public float yRemainder = 0f;
}
