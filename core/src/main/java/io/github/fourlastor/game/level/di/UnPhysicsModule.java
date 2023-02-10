package io.github.fourlastor.game.level.di;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.system.ClearScreenSystem;
import io.github.fourlastor.game.level.system.StageSystem;

@Module
public class UnPhysicsModule {

    @Provides
    @ScreenScoped
    public Engine engine(
            StageSystem stageSystem,
            ClearScreenSystem clearScreenSystem) {
        Engine engine = new Engine();
        engine.addSystem(clearScreenSystem);
        engine.addSystem(stageSystem);
        return engine;
    }

    @Provides
    @ScreenScoped
    public Viewport viewport(GameConfig config) {
        return new FitViewport(config.display.width, config.display.height);
    }

    @Provides
    @ScreenScoped
    public Stage stage(Viewport viewport) {
        return new Stage(viewport);
    }

    @Provides
    @ScreenScoped
    public Camera camera(Viewport viewport) {
        return viewport.getCamera();
    }

    @Provides
    @ScreenScoped
    public World world(GameConfig config) {
        return new World(config.physics.gravity, true);
    }

    @Provides
    @ScreenScoped
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }

    @Provides
    public GameConfig gameConfig() {
        return new GameConfig(
                new GameConfig.Display(16f, 9f, 1f / 16f),
                new GameConfig.Physics(new Vector2(0f, -10f)),
                new GameConfig.Player(8f, 0.1f, 1f, 4f, 0.2f, new Vector2(0f, -20f)),
                new GameConfig.Entities(0.7f));
    }
}
