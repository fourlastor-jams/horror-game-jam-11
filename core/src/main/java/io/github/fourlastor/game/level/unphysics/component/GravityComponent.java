package io.github.fourlastor.game.level.unphysics.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class GravityComponent implements Component {

    public final Vector2 gravity;

    public GravityComponent(Vector2 gravity) {
        this.gravity = gravity;
    }
}
