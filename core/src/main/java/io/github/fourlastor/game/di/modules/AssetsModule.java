package io.github.fourlastor.game.di.modules;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.harlequin.animation.Animation;
import io.github.fourlastor.harlequin.animation.AnimationNode;
import io.github.fourlastor.harlequin.loader.dragonbones.DragonBonesLoader;
import io.github.fourlastor.harlequin.loader.dragonbones.model.DragonBonesEntity;
import io.github.fourlastor.harlequin.loader.spine.SpineLoader;
import io.github.fourlastor.harlequin.loader.spine.model.SpineEntity;
import io.github.fourlastor.json.JsonParser;
import io.github.fourlastor.ldtk.LdtkLoader;
import io.github.fourlastor.ldtk.model.LdtkMapData;
import io.github.fourlastor.text.Text;
import io.github.fourlastor.text.TextLoader;
import java.util.HashMap;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class AssetsModule {

    private static final String PATH_TEXTURE_ATLAS = "images/included/packed/images.pack.atlas";
    public static final String PATH_LEVELS = "maps/levels.ldtk";
    private static final String PATH_DEFAULT_SHADER = "shaders/default.vs";
    private static final String PATH_WAVE_SHADER = "shaders/wave.fs";
    public static final String WHITE_PIXEL = "white-pixel";
    private static final String CHARACTER_ANIMATION_PATH = "images/included/animations/character/character.json";

    @Provides
    public DragonBonesLoader dragonBonesLoader(JsonReader json, JsonParser<DragonBonesEntity> parser) {
        return new DragonBonesLoader(
                new DragonBonesLoader.Options(PATH_TEXTURE_ATLAS, "images/included"), json, parser);
    }

    @Provides
    @Singleton
    public AssetManager assetManager(
            LdtkLoader ldtkLoader,
            TextLoader textLoader,
            SpineLoader spineLoader,
            DragonBonesLoader dragonBonesLoader) {
        AssetManager assetManager = new AssetManager();
        assetManager.setLoader(LdtkMapData.class, ldtkLoader);
        assetManager.setLoader(Text.class, textLoader);
        assetManager.setLoader(SpineEntity.class, spineLoader);
        assetManager.setLoader(AnimationNode.Group.class, dragonBonesLoader);
        assetManager.load(PATH_TEXTURE_ATLAS, TextureAtlas.class);
        assetManager.load(PATH_LEVELS, LdtkMapData.class);
        HashMap<String, Animation.PlayMode> playModes = new HashMap<>();
        playModes.put("dead", Animation.PlayMode.NORMAL);
        assetManager.load(
                CHARACTER_ANIMATION_PATH, AnimationNode.Group.class, new DragonBonesLoader.Parameters(playModes));
        assetManager.load(PATH_DEFAULT_SHADER, Text.class);
        assetManager.load(PATH_WAVE_SHADER, Text.class);
        assetManager.finishLoading();
        return assetManager;
    }

    @Provides
    @Singleton
    public TextureAtlas textureAtlas(AssetManager assetManager) {
        return assetManager.get(PATH_TEXTURE_ATLAS, TextureAtlas.class);
    }

    @Provides
    @Singleton
    @Named(WHITE_PIXEL)
    public TextureRegion whitePixel(TextureAtlas atlas) {
        return atlas.findRegion("whitePixel");
    }
}
