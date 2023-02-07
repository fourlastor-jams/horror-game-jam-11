package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.ldtk.model.LdtkDefinitions;
import io.github.fourlastor.ldtk.model.LdtkLayerInstance;
import io.github.fourlastor.ldtk.model.LdtkLevelDefinition;
import io.github.fourlastor.ldtk.model.LdtkTileInstance;
import io.github.fourlastor.ldtk.model.LdtkTilesetDefinition;
import io.github.fourlastor.ldtk.scene2d.LdtkMapParser;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory to create various entities: player, buildings, enemies..
 */
@ScreenScoped
public class EntitiesFactory {

    private final TextureAtlas atlas;
    private final LdtkDefinitions definitions;
    private final LdtkLevelDefinition definition;
    private final GameConfig config;

    @Inject
    public EntitiesFactory(TextureAtlas atlas, LdtkDefinitions definitions, LdtkLevelDefinition definition, GameConfig config) {
        this.atlas = atlas;
        this.definitions = definitions;
        this.definition = definition;
        this.config = config;
    }

    public List<Entity> tiles() {
        LdtkMapParser parser = new LdtkMapParser(atlas, "tiles", config.scale);
        List<Entity> entities = new ArrayList<>();

        WidgetGroup layers = new WidgetGroup();
        List<LdtkLayerInstance> layerInstances =
                definition.layerInstances == null ? new ArrayList<>() : definition.layerInstances;
        for (LdtkLayerInstance layerInstance : layerInstances) {
            if (!"AutoLayer".equals(layerInstance.type)) {
                continue;
            }
            LdtkTilesetDefinition tileset = definitions.tileset(layerInstance.tilesetDefUid);
            for (LdtkTileInstance tileInstance : layerInstance.autoLayerTiles) {
                Image tile = parser.tile(layerInstance, tileset, tileInstance);
                Entity entity = new Entity();
                entity.add(new ActorComponent(tile, ActorComponent.Layer.BG_PARALLAX));
                entities.add(entity);
            }
        }
        return entities;
    }

}
