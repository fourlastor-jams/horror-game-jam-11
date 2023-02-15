package io.github.fourlastor.game.level.unphysics.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;

import java.util.HashSet;
import java.util.Set;

public class KinematicBodyComponent implements Component {
    public final GridPoint2 collision = new GridPoint2();
    public final GridPoint2 touching = new GridPoint2();

    public final Set<Entity> sensors = new HashSet<>();
}
