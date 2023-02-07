package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import javax.inject.Inject;

public class LevelScreen extends ScreenAdapter {

    private final Engine engine;
    private final Viewport viewport;

    private final World world;
    private final EntitiesFactory factory;

    @Inject
    public LevelScreen(Engine engine, Viewport viewport, World world, EntitiesFactory factory) {
        this.engine = engine;
        this.viewport = viewport;
        this.world = world;
        this.factory = factory;
    }

    @Override
    public void show() {
        super.show();
        for (Entity tile : factory.tiles()) {
            engine.addEntity(tile);
        }
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
