package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.FollowBodyComponent;
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import io.github.fourlastor.game.route.Router;
import javax.inject.Inject;

public class UnPhysicsScreen extends ScreenAdapter {

    private final Engine engine;
    private final Viewport viewport;

    private final InputMultiplexer input;
    private final Router router;
    private final TextureAtlas atlas;

    @Inject
    public UnPhysicsScreen(
            Engine engine, Viewport viewport, InputMultiplexer input, Router router, TextureAtlas atlas) {
        this.engine = engine;
        this.viewport = viewport;
        this.input = input;
        this.router = router;
        this.atlas = atlas;
    }

    @Override
    public void show() {
        super.show();
        input.addProcessor(restartOnR);
        TextureAtlas.AtlasRegion pixel = atlas.findRegion("whitePixel");
        float size = 16f;
        for (int i = 0; i < 10; i++) {
            float x = i * size;
            Entity entity = new Entity();
            Image whitePixel = new Image(pixel);
            whitePixel.setSize(size, size);
            entity.add(new ActorComponent(whitePixel, ActorComponent.Layer.BG_PARALLAX));
            entity.add(new FollowBodyComponent());
            entity.add(new TransformComponent(new Transform(new Rectangle(x, 0f, size, size))));
            entity.add(new SolidBodyComponent());
            engine.addEntity(entity);
        }

        Entity entity = new Entity();
        Image whitePixel = new Image(pixel);
        whitePixel.setSize(size, size);
        whitePixel.setColor(Color.PINK);
        entity.add(new ActorComponent(whitePixel, ActorComponent.Layer.BG_PARALLAX));
        entity.add(new FollowBodyComponent());
        entity.add(new TransformComponent(new Transform(new Rectangle(5f, 4f * size, size, size))));
        entity.add(new KinematicBodyComponent());
        entity.add(new MovingBodyComponent());
        entity.add(new GravityComponent(new Vector2(0, -160f)));
        engine.addEntity(entity);
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
