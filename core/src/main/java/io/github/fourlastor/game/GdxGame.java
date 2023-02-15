package io.github.fourlastor.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import io.github.fourlastor.game.di.GameComponent;
import io.github.fourlastor.game.intro.IntroComponent;
import io.github.fourlastor.game.level.di.LevelComponent;
import io.github.fourlastor.game.level.di.MapModule;
import io.github.fourlastor.game.route.Router;
import io.github.fourlastor.game.route.RouterModule;

public class GdxGame extends Game implements Router {

    private final InputMultiplexer multiplexer;

    private final LevelComponent.Builder levelScreenFactory;
    private final IntroComponent.Builder introScreenFactory;

    private Screen pendingScreen = null;

    public GdxGame(
            InputMultiplexer multiplexer,
            LevelComponent.Builder levelScreenFactory,
            IntroComponent.Builder introScreenFactory) {
        this.multiplexer = multiplexer;
        this.levelScreenFactory = levelScreenFactory;
        this.introScreenFactory = introScreenFactory;
    }

    @Override
    public void create() {
        Gdx.input.setInputProcessor(multiplexer);
        goToLevel(0);
    }

    @Override
    public void render() {
        if (pendingScreen != null) {
            setScreen(pendingScreen);
            pendingScreen = null;
        }
        super.render();
    }

    public static GdxGame createGame() {
        return GameComponent.component().game();
    }

    @Override
    public void goToIntro() {
        pendingScreen =
                introScreenFactory.router(new RouterModule(this)).build().screen();
    }

    @Override
    public void goToLevel(int levelIndex) {
        pendingScreen = levelScreenFactory
                .router(new RouterModule(this))
                .map(new MapModule(levelIndex))
                .build()
                .screen();
    }
}
