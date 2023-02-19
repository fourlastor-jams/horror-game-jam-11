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
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorSystem;
import io.github.fourlastor.game.level.input.CharacterStateSystem;
import io.github.fourlastor.game.level.input.InputBufferSystem;
import io.github.fourlastor.game.level.system.AreaSystem;
import io.github.fourlastor.game.level.system.CameraMovementSystem;
import io.github.fourlastor.game.level.system.ClearScreenSystem;
import io.github.fourlastor.game.level.system.MovingSystem;
import io.github.fourlastor.game.level.system.StageSystem;
import io.github.fourlastor.game.level.unphysics.system.ActorFollowTransformSystem;
import io.github.fourlastor.game.level.unphysics.system.BodyMovingSystem;
import io.github.fourlastor.game.level.unphysics.system.GravitySystem;
import io.github.fourlastor.game.level.unphysics.system.TransformDebugSystem;

@Module
public class LevelModule {

    @Provides
    @ScreenScoped
    public Engine engine(
            InputBufferSystem inputBufferSystem,
            CharacterStateSystem characterStateSystem,
            TimedFloorSystem timedFloorSystem,
            CameraMovementSystem cameraMovementSystem,
            StageSystem stageSystem,
            ClearScreenSystem clearScreenSystem,
            BodyMovingSystem bodyMovingSystem,
            GravitySystem gravitySystem,
            MovingSystem movingSystem,
            ActorFollowTransformSystem actorFollowTransformSystem,
            AreaSystem areaSystem,
            @SuppressWarnings("unused") // debug only
                    TransformDebugSystem transformDebugSystem) {
        Engine engine = new Engine();
        engine.addSystem(inputBufferSystem);
        engine.addSystem(characterStateSystem);
        engine.addSystem(timedFloorSystem);
        engine.addSystem(bodyMovingSystem);
        engine.addSystem(gravitySystem);
        engine.addSystem(movingSystem);
        engine.addSystem(areaSystem);
        engine.addSystem(actorFollowTransformSystem);
        engine.addSystem(cameraMovementSystem);
        engine.addSystem(clearScreenSystem);
        engine.addSystem(stageSystem);
        engine.addSystem(transformDebugSystem);
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
                new GameConfig.Display(256f * 3, 144f * 3, 1f),
                new GameConfig.Physics(new Vector2(0f, -216)),
                new GameConfig.Player(96f, 0.1f, 16f, 48f, 144f, 0.2f, 2f),
                new GameConfig.Entities(0.7f));
    }
}
