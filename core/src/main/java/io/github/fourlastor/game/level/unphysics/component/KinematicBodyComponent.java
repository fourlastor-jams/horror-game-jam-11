package io.github.fourlastor.game.level.unphysics.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;

public class KinematicBodyComponent implements Component {
    public final GridPoint2 collision = new GridPoint2();
}
