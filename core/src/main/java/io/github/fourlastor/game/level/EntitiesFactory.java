package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
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
import io.github.fourlastor.game.level.entity.falseFloor.FalseFloorComponent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

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

    @SuppressWarnings("unused") // dev only
    public Entity origin() {
        Entity entity = new Entity();
        entity.add(new ActorComponent(new Image(atlas.findRegion("whitePixel")), ActorComponent.Layer.CHARACTER));
        return entity;
    }

    public List<Entity> tiles() {
        float scale = config.display.scale;
        LdtkMapParser parser = new LdtkMapParser(atlas, "tiles", scale);
        List<Entity> entities = new ArrayList<>();

        final float tileSize = 16f * scale;
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
                                    tileInstance.x() * scale,
                                    tileInstance.y(layerInstance.cHei, layerInstance.gridSize) * scale)
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
        float scale = config.display.scale;
        animation.setOrigin(Align.left);
        float halfScale = scale / 2;
        animation.setScale(halfScale, halfScale);
        entity.add(new ActorComponent(animation, ActorComponent.Layer.CHARACTER));
        entity.add(new FollowBodyComponent());
        LdtkLayerInstance entityLayer = entityLayer();
        LdtkEntityInstance playerSpawn = playerSpawn(entityLayer);
        entity.add(new BodyBuilderComponent(world -> {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            float halfWidth = playerSpawn.halfWidth() * halfScale;
            float halfHeight = playerSpawn.halfHeight() * halfScale;
            def.position.set(
                    halfWidth + playerSpawn.x() * scale,
                    halfHeight + playerSpawn.y(entityLayer.cHei, entityLayer.gridSize) * scale);
            Body body = world.createBody(def);
            FixtureDef fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(halfWidth, halfHeight, new Vector2(0f, -halfHeight), 0f);
            fixtureDef.shape = shape;
            fixtureDef.friction = 0f;
            fixtureDef.filter.categoryBits = Bits.Category.PLAYER.bits;
            fixtureDef.filter.maskBits = Bits.Mask.PLAYER.bits;
            body.createFixture(fixtureDef);
            shape.setAsBox(halfWidth * 0.9f, 0.03f, new Vector2(0f, -halfHeight * 2), 0f);
            fixtureDef.isSensor = true;
            fixtureDef.filter.categoryBits = Bits.Category.PLAYER_FOOT.bits;
            fixtureDef.filter.maskBits = Bits.Mask.PLAYER_FOOT.bits;
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

    public List<Entity> spikes() {
        float scale = config.display.scale;
        LdtkLayerInstance entityLayer = entityLayer();
        List<Entity> entities = new ArrayList<>();
        for (LdtkEntityInstance instance : entityLayer.entityInstances) {
            if (instance.identifier.startsWith("Spike")) {
                Entity entity = new Entity();
                String tileName = instance.identifier.toLowerCase(Locale.ROOT).replace('_', '-');
                Image actor = new Image(atlas.findRegion("entities/" + tileName));
                actor.setScale(scale);
                float x = instance.x();
                float y = instance.y(entityLayer.cHei, entityLayer.gridSize);
                actor.setPosition(x * scale, y * scale);
                entity.add(new ActorComponent(actor, ActorComponent.Layer.PLATFORM));
                entity.add(new BodyBuilderComponent(world -> {
                    BodyDef def = new BodyDef();
                    def.type = BodyDef.BodyType.StaticBody;
                    def.position.set((x + instance.halfWidth()) * scale, (y + instance.halfHeight()) * scale);
                    Body body = world.createBody(def);
                    FixtureDef fixtureDef = new FixtureDef();
                    PolygonShape shape = new PolygonShape();
                    float ratio = config.entities.spikeSizeRatio;
                    float invertedRatio = 1f - ratio;
                    float centerX = scale
                            * instance.halfWidth()
                            * invertedRatio
                            * (tileName.endsWith("left") ? -1f : tileName.endsWith("right") ? 1f : 0f);
                    float centerY = scale
                            * instance.halfHeight()
                            * invertedRatio
                            * (tileName.endsWith("bottom") ? -1f : tileName.endsWith("top") ? 1f : 0f);
                    shape.setAsBox(
                            instance.halfWidth() * scale * ratio,
                            instance.halfHeight() * scale * ratio,
                            new Vector2(centerX, centerY),
                            0f);
                    fixtureDef.shape = shape;
                    fixtureDef.isSensor = true;
                    fixtureDef.filter.categoryBits = Bits.Category.SPIKE.bits;
                    fixtureDef.filter.maskBits = Bits.Mask.SPIKE.bits;
                    body.createFixture(fixtureDef);
                    return body;
                }));
                entities.add(entity);
            }
        }
        return entities;
    }

    public List<Entity> falseFloors() {
        float scale = config.display.scale;
        LdtkLayerInstance entityLayer = entityLayer();
        List<Entity> entities = new ArrayList<>();
        for (LdtkEntityInstance instance : entityLayer.entityInstances) {
            if ("False_floor".equals(instance.identifier)) {
                Entity entity = new Entity();
                String tileName = instance.identifier.toLowerCase(Locale.ROOT).replace('_', '-');
                Image actor = new Image(atlas.findRegion("entities/" + tileName));
                actor.setScale(scale);
                float x = instance.x();
                float y = instance.y(entityLayer.cHei, entityLayer.gridSize);
                actor.setPosition(x * scale, y * scale);
                entity.add(new ActorComponent(actor, ActorComponent.Layer.PLATFORM));
                entity.add(new BodyBuilderComponent(world -> {
                    BodyDef def = new BodyDef();
                    def.type = BodyDef.BodyType.StaticBody;
                    def.position.set((x + instance.halfWidth()) * scale, (y + instance.halfHeight()) * scale);
                    Body body = world.createBody(def);
                    FixtureDef fixtureDef = new FixtureDef();

                    ChainShape shape = new ChainShape();

                    float centerAdjustX = instance.halfWidth() * scale;
                    float centerAdjustY = instance.halfHeight() * scale * 3f / 16f;

                    float[] vertices = new float[] {
                        -centerAdjustX,
                        -centerAdjustY,
                        -centerAdjustX,
                        centerAdjustY,
                        centerAdjustX,
                        centerAdjustY,
                        centerAdjustX,
                        -centerAdjustY,
                    };
                    shape.createLoop(vertices);
                    fixtureDef.shape = shape;
                    fixtureDef.filter.categoryBits = Bits.Category.GROUND.bits;
                    fixtureDef.filter.maskBits = Bits.Mask.GROUND.bits;
                    Fixture fixture = body.createFixture(fixtureDef);
                    fixture.setUserData(entity);
                    return body;
                }));
                entity.add(new FalseFloorComponent.Request());
                entities.add(entity);
            }
        }
        return entities;
    }
}
