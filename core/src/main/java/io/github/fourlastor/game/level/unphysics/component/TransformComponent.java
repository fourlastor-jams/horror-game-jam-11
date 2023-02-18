package io.github.fourlastor.game.level.unphysics.component;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.unphysics.Transform;

public class TransformComponent implements Component {

    public final Transform transform;

    public TransformComponent(Transform transform) {
        this.transform = transform;
    }
}
