package io.github.fourlastor.game.level.di;

import dagger.Subcomponent;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.UnPhysicsScreen;
import io.github.fourlastor.game.route.RouterModule;

@ScreenScoped
@Subcomponent(modules = {UnPhysicsModule.class, MapModule.class, RouterModule.class, EcsModule.class})
public interface UnPhysicsComponent {

    @ScreenScoped
    UnPhysicsScreen screen();

    @Subcomponent.Builder
    interface Builder {

        Builder router(RouterModule routerModule);

        Builder map(MapModule mapModule);

        UnPhysicsComponent build();
    }
}
