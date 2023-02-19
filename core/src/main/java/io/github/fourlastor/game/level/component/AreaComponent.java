package io.github.fourlastor.game.level.component;

import com.badlogic.ashley.core.Component;
import io.github.fourlastor.game.level.Area;

public class AreaComponent implements Component {
    public final Area area;

    public AreaComponent(Area area) {
        this.area = area;
    }
}
