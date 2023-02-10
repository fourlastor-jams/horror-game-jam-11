package io.github.fourlastor.game.di.modules;

import dagger.Module;
import io.github.fourlastor.game.intro.IntroComponent;
import io.github.fourlastor.game.level.di.LevelComponent;
import io.github.fourlastor.game.level.di.UnPhysicsComponent;

@Module(
        subcomponents = {
            LevelComponent.class,
            IntroComponent.class,
            UnPhysicsComponent.class,
        })
public class ScreensModule {}
