package io.github.fourlastor.game.level.di;

import com.badlogic.gdx.assets.AssetManager;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.di.modules.AssetsModule;
import io.github.fourlastor.ldtk.model.LdtkDefinitions;
import io.github.fourlastor.ldtk.model.LdtkLevelDefinition;
import io.github.fourlastor.ldtk.model.LdtkMapData;

@Module
public class MapModule {


    private final int levelIndex;


    public MapModule(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    @Provides
    public LdtkLevelDefinition definition(AssetManager assetManager) {
        return getLdtkMapData(assetManager).levels.get(levelIndex);
    }

    @Provides
    public LdtkDefinitions definitions(AssetManager assetManager) {
        return getLdtkMapData(assetManager).defs;
    }

    private LdtkMapData getLdtkMapData(AssetManager assetManager) {
        return assetManager.get(AssetsModule.PATH_LEVELS);
    }
}
