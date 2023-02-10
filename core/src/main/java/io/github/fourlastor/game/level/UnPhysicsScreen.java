package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.route.Router;

import javax.inject.Inject;

public class UnPhysicsScreen extends ScreenAdapter {

    private final Engine engine;
    private final Viewport viewport;


    private final InputMultiplexer input;
    private final Router router;

    @Inject
    public UnPhysicsScreen(
            Engine engine,
            Viewport viewport,
            InputMultiplexer input,
            Router router) {
        this.engine = engine;
        this.viewport = viewport;
        this.input = input;
        this.router = router;
    }

    @Override
    public void show() {
        super.show();
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
        super.dispose();
    }
}
