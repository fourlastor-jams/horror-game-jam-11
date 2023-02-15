package io.github.fourlastor.game.level.unphysics.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.ObjectSet;

public class KinematicBodyComponent implements Component {
    public final GridPoint2 collision = new GridPoint2();
    public final GridPoint2 touching = new GridPoint2();

    public final ObjectSet<Entity> sensors = new ObjectSet<>(10);
}
