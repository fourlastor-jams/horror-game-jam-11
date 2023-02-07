package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Entity;
import io.github.fourlastor.game.di.ScreenScoped;

import javax.inject.Inject;
import java.util.List;

/**
 * Factory to create various entities: player, buildings, enemies..
 */
@ScreenScoped
public class EntitiesFactory {

    @Inject
    public EntitiesFactory() {
    }

}
