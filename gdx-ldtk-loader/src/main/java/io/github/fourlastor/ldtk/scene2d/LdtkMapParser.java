package io.github.fourlastor.ldtk.scene2d;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import io.github.fourlastor.ldtk.model.LdtkDefinitions;
import io.github.fourlastor.ldtk.model.LdtkLayerInstance;
import io.github.fourlastor.ldtk.model.LdtkLevelDefinition;
import io.github.fourlastor.ldtk.model.LdtkTileInstance;
import io.github.fourlastor.ldtk.model.LdtkTilesetCustomData;
import io.github.fourlastor.ldtk.model.LdtkTilesetDefinition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LdtkMapParser {

    private final TextureAtlas atlas;
    private final String basePath;
    private final HashMap<String, TextureRegion> tilesCache = new HashMap<>();

    public LdtkMapParser(TextureAtlas atlas, String basePath) {
        this.atlas = atlas;
        this.basePath = basePath;
    }

    public WidgetGroup parse(LdtkLevelDefinition definition, LdtkDefinitions definitions) {
        WidgetGroup layers = new WidgetGroup();
        List<LdtkLayerInstance> layerInstances =
                definition.layerInstances == null ? new ArrayList<>() : definition.layerInstances;
        for (LdtkLayerInstance layerInstance : layerInstances) {
            if (!"AutoLayer".equals(layerInstance.type)) {
                continue;
            }
            WidgetGroup layer = new WidgetGroup();
            LdtkTilesetDefinition tileset = definitions.tileset(layerInstance.tilesetDefUid);
            for (LdtkTileInstance tileInstance : layerInstance.autoLayerTiles) {
                LdtkTilesetCustomData customData = tileset.customData(tileInstance.t);
                String tileName = basePath + "/" + customData.data;
                Image tile = new Image(getAtlasRegion(tileName, tileInstance.flipX(), tileInstance.flipY()));

                tile.setPosition(tileInstance.x() / 16f, layerInstance.cHei - tileInstance.y() / 16f);
                tile.setScale(1f / 16f);
                layer.addActor(tile);
            }
            layers.addActor(layer);
        }
        return layers;
    }

    private TextureRegion getAtlasRegion(String tileName, boolean flipX, boolean flipY) {
        String key = tileName + (flipX ? "_x" : "") + (flipY ? "_y" : "");
        if (!tilesCache.containsKey(key)) {
            TextureRegion region = new TextureRegion(atlas.findRegion(tileName));
            region.flip(flipX, flipY);
            tilesCache.put(key, region);
        }
        return tilesCache.get(key);
    }
}
