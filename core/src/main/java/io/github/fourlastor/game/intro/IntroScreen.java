package io.github.fourlastor.game.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.fourlastor.game.di.modules.AssetsModule;
import io.github.fourlastor.harlequin.animation.AnimationNode;
import io.github.fourlastor.harlequin.ui.AnimationStateMachine;
import io.github.fourlastor.ldtk.model.LdtkMapData;
import io.github.fourlastor.ldtk.scene2d.LdtkMapParser;
import javax.inject.Inject;

public class IntroScreen extends ScreenAdapter {

    public static final Color CLEAR_COLOR = Color.valueOf("cccccc");
    private static final float SCALE = 1f / 16f;
    private static final float CHARACTER_SCALE = SCALE / 2;

    private final InputMultiplexer inputMultiplexer;
    private final Stage stage;
    private final Viewport viewport;
    private final AnimationStateMachine animationGroup;
    private final OrthographicCamera camera;

    @Inject
    public IntroScreen(InputMultiplexer inputMultiplexer, AssetManager assetManager, TextureAtlas atlas) {
        this.inputMultiplexer = inputMultiplexer;

        camera = new OrthographicCamera();
        viewport = new FitViewport(16, 9, camera);
        stage = new Stage(viewport);
        LdtkMapParser mapParser = new LdtkMapParser(atlas, "tiles", SCALE);
        LdtkMapData definition = assetManager.get(AssetsModule.PATH_LEVELS);
        WidgetGroup tilesGroup = mapParser.parse(definition.levels.get(0), definition.defs);
        stage.addActor(tilesGroup);

        AnimationNode.Group node =
                assetManager.get("images/included/animations/character/character.json", AnimationNode.Group.class);
        animationGroup = new AnimationStateMachine(node);
        animationGroup.setScale(CHARACTER_SCALE);
        stage.addActor(animationGroup);
    }

    @Override
    public void show() {
        inputMultiplexer.addProcessor(processor);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        inputMultiplexer.removeProcessor(processor);
    }

    private final InputProcessor processor = new InputAdapter() {
        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.A:
                    animationGroup.enter("idle");
                    return true;
                case Input.Keys.S:
                    animationGroup.enter("run");
                    return true;
                case Input.Keys.D:
                    camera.zoom *= 2;
                    camera.update();
                    return true;
                case Input.Keys.F:
                    camera.zoom /= 2;
                    camera.update();
                    return true;
                case Input.Keys.G:
                    //                    animationGroup.enter("snap");
                    return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            System.out.println("Go to level");
            return true;
        }
    };

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(CLEAR_COLOR.r, CLEAR_COLOR.g, CLEAR_COLOR.b, CLEAR_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
}
