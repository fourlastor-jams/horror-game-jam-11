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
import io.github.fourlastor.game.level.input.CharacterStateSystem;
import io.github.fourlastor.game.level.input.InputBufferSystem;
import io.github.fourlastor.game.level.physics.PhysicsDebugSystem;
import io.github.fourlastor.game.level.physics.PhysicsSystem;
import io.github.fourlastor.game.level.system.ActorFollowBodySystem;
import io.github.fourlastor.game.level.system.CameraMovementSystem;
import io.github.fourlastor.game.level.system.ClearScreenSystem;
import io.github.fourlastor.game.level.system.MovingSystem;
import io.github.fourlastor.game.level.system.SoundSystem;
import io.github.fourlastor.game.level.system.StageSystem;

@Module
public class LevelModule {

    @Provides
    @ScreenScoped
    public Engine engine(
            InputBufferSystem inputBufferSystem,
            CharacterStateSystem characterStateSystem,
            CameraMovementSystem cameraMovementSystem,
            PhysicsSystem physicsSystem,
            ActorFollowBodySystem actorFollowBodySystem,
            StageSystem stageSystem,
            ClearScreenSystem clearScreenSystem,
            @SuppressWarnings("unused") // debug only
                    PhysicsDebugSystem physicsDebugSystem,
            MovingSystem movingSystem,
            SoundSystem soundSystem) {
        Engine engine = new Engine();
        engine.addSystem(movingSystem);
        engine.addSystem(inputBufferSystem);
        engine.addSystem(characterStateSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(soundSystem);
        engine.addSystem(cameraMovementSystem);
        engine.addSystem(actorFollowBodySystem);
        engine.addSystem(clearScreenSystem);
        engine.addSystem(stageSystem);
        engine.addSystem(physicsDebugSystem);
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
                new GameConfig.Player(4f, 1f, 5f, 0.2f, new Vector2(0f, -20f)));
    }
}
