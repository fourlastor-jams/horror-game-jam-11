package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.BodyBuilderComponent;
import io.github.fourlastor.ldtk.model.LdtkDefinitions;
import io.github.fourlastor.ldtk.model.LdtkLayerInstance;
import io.github.fourlastor.ldtk.model.LdtkLevelDefinition;
import io.github.fourlastor.ldtk.model.LdtkTileInstance;
import io.github.fourlastor.ldtk.model.LdtkTilesetDefinition;
import io.github.fourlastor.ldtk.scene2d.LdtkMapParser;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

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
    public EntitiesFactory(
            TextureAtlas atlas, LdtkDefinitions definitions, LdtkLevelDefinition definition, GameConfig config) {
        this.atlas = atlas;
        this.definitions = definitions;
        this.definition = definition;
        this.config = config;
    }

    public List<Entity> tiles() {
        LdtkMapParser parser = new LdtkMapParser(atlas, "tiles", config.scale);
        List<Entity> entities = new ArrayList<>();

        final float tileSize = 16f * config.scale;
        final float centerAdjust = tileSize / 2;
        float[] vertices = new float[] {
            -centerAdjust,
            -centerAdjust,
            -centerAdjust,
            centerAdjust,
            centerAdjust,
            centerAdjust,
            centerAdjust,
            -centerAdjust,
        };

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
                entity.add(new BodyBuilderComponent(world -> {
                    BodyDef def = new BodyDef();
                    def.position
                            .set(
                                    tileInstance.x() * config.scale,
                                    tileInstance.y(layerInstance.cHei, layerInstance.gridSize) * config.scale)
                            .add(centerAdjust, centerAdjust);
                    def.type = BodyDef.BodyType.StaticBody;
                    Body body = world.createBody(def);
                    FixtureDef fixtureDef = new FixtureDef();
                    ChainShape shape = new ChainShape();
                    shape.createLoop(vertices);
                    fixtureDef.shape = shape;
                    body.createFixture(fixtureDef);
                    shape.dispose();
                    return body;
                }));
                entities.add(entity);
            }
        }
        return entities;
    }
}
