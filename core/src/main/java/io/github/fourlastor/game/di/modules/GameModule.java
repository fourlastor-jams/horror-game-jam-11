package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.InputMultiplexer;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.GdxGame;
import io.github.fourlastor.game.intro.IntroComponent;
import io.github.fourlastor.game.level.di.LevelComponent;
import io.github.fourlastor.game.level.di.UnPhysicsComponent;

import java.util.Random;
import javax.inject.Singleton;

@Module
public class GameModule {

    @Provides
    @Singleton
    public GdxGame game(
            InputMultiplexer multiplexer,
            LevelComponent.Builder levelBuilder,
            IntroComponent.Builder introBuilder,
            UnPhysicsComponent.Builder unPhysicsScreenFactory) {
        return new GdxGame(multiplexer, levelBuilder, introBuilder, unPhysicsScreenFactory);
    }

    @Provides
    public Random random() {
        return new Random();
    }
}
