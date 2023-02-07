package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.inject.Inject;

public class LevelScreen extends ScreenAdapter {

    private final Engine engine;
    private final Viewport viewport;

    private final World world;

    @Inject
    public LevelScreen(
            Engine engine,
            Viewport viewport,
            World world) {
        this.engine = engine;
        this.viewport = viewport;
        this.world = world;
    }

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
        super.dispose();
    }
}
