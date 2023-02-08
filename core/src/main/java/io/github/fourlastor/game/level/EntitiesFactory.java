package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyBuilderComponent;
import io.github.fourlastor.game.level.component.FollowBodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerRequestComponent;
import io.github.fourlastor.game.level.input.controls.Controls;
import io.github.fourlastor.game.level.physics.Bits;
import io.github.fourlastor.harlequin.animation.AnimationNode;
import io.github.fourlastor.harlequin.ui.AnimationStateMachine;
import io.github.fourlastor.ldtk.model.LdtkDefinitions;
import io.github.fourlastor.ldtk.model.LdtkEntityInstance;
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
@SuppressWarnings("DataFlowIssue")
public class EntitiesFactory {

    private final AssetManager assetManager;
    private final TextureAtlas atlas;
    private final LdtkDefinitions definitions;
    private final LdtkLevelDefinition definition;
    private final GameConfig config;

    @Inject
    public EntitiesFactory(
            AssetManager assetManager,
            TextureAtlas atlas,
            LdtkDefinitions definitions,
            LdtkLevelDefinition definition,
            GameConfig config) {
        this.assetManager = assetManager;
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
                    fixtureDef.filter.categoryBits = Bits.Category.GROUND.bits;
                    fixtureDef.filter.maskBits = Bits.Mask.GROUND.bits;
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

    public Entity character() {
        Entity entity = new Entity();
        AnimationNode.Group animationNode =
                assetManager.get("images/included/animations/character/character.json", AnimationNode.Group.class);
        AnimationStateMachine animation = new AnimationStateMachine(animationNode);
        float scale = config.scale / 2;
        animation.setOrigin(Align.left);
        animation.setScale(scale, scale);
        entity.add(new ActorComponent(animation, ActorComponent.Layer.CHARACTER));
        entity.add(new FollowBodyComponent());
        LdtkLayerInstance entityLayer = entityLayer();
        LdtkEntityInstance playerSpawn = playerSpawn(entityLayer);
        entity.add(new BodyBuilderComponent(world -> {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            float halfWidth = playerSpawn.width / 2f * scale;
            float halfHeight = playerSpawn.height / 2f * scale;
            def.position.set(
                    halfWidth + playerSpawn.x() * config.scale,
                    halfHeight + playerSpawn.y(entityLayer.cHei, entityLayer.gridSize) * config.scale);
            Body body = world.createBody(def);
            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(halfWidth, halfHeight);
            fixtureDef.shape = shape;
            fixtureDef.friction = 0f;
            fixtureDef.filter.categoryBits = Bits.Category.PLAYER.bits;
            fixtureDef.filter.maskBits = Bits.Mask.PLAYER.bits;
            body.createFixture(fixtureDef);
            shape.dispose();
            return body;
        }));
        entity.add(new AnimatedComponent(animation));
        entity.add(new PlayerRequestComponent(Controls.Setup.P1));
        entity.add(new InputComponent());
        return entity;
    }

    private LdtkEntityInstance playerSpawn(LdtkLayerInstance ldtkLayerInstance) {
        for (LdtkEntityInstance instance : ldtkLayerInstance.entityInstances) {
            if ("Player".equals(instance.identifier)) {
                return instance;
            }
        }
        throw new IllegalStateException("Missing player spawn point");
    }

    private LdtkLayerInstance entityLayer() {

        for (LdtkLayerInstance layerInstance : definition.layerInstances) {
            if ("Entities".equals(layerInstance.type)) {
                return layerInstance;
            }
        }
        throw new IllegalStateException("Missing entities layer");
    }
}
