package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.route.Router;
import javax.inject.Inject;

public class LevelScreen extends ScreenAdapter {

    private final Engine engine;
    private final Viewport viewport;

    private final World world;
    private final EntitiesFactory factory;

    private final InputMultiplexer input;
    private final Router router;
    private final Stage stage;

    @Inject
    public LevelScreen(
            Engine engine,
            Viewport viewport,
            World world,
            EntitiesFactory factory,
            InputMultiplexer input,
            Router router,
            Stage stage) {
        this.engine = engine;
        this.viewport = viewport;
        this.world = world;
        this.factory = factory;
        this.input = input;
        this.router = router;
        this.stage = stage;
    }

    @Override
    public void show() {
        super.show();
        for (Entity tile : factory.tiles()) {
            engine.addEntity(tile);
        }
        for (Entity tile : factory.spikes()) {
            engine.addEntity(tile);
        }
        for (Entity tile : factory.falseFloors()) {
            engine.addEntity(tile);
        }
        engine.addEntity(factory.character());
        input.addProcessor(restartOnR);
    }

    @Override
    public void hide() {
        input.removeProcessor(restartOnR);
        super.hide();
    }

    private final InputAdapter restartOnR = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.R) {
                router.goToLevel(0);
                return true;
            }
            return super.keyDown(keycode);
        }
    };

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render(float delta) {
        engine.update(delta);
    }

    @Override
    public void dispose() {
        engine.removeAllEntities();
        engine.removeAllSystems();
        world.dispose();
        stage.dispose();
        super.dispose();
    }
}
