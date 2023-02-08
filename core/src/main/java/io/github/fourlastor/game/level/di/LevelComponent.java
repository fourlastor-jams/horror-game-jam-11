package io.github.fourlastor.game.level.di;

import dagger.Subcomponent;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.LevelScreen;
import io.github.fourlastor.game.route.RouterModule;

@ScreenScoped
@Subcomponent(
        modules = {
            LevelModule.class,
            MapModule.class,
            RouterModule.class,
            EcsModule.class
        })
public interface LevelComponent {

    @ScreenScoped
    LevelScreen screen();

    @Subcomponent.Builder
    interface Builder {

        Builder router(RouterModule routerModule);

        Builder map(MapModule mapModule);

        LevelComponent build();
    }
}
