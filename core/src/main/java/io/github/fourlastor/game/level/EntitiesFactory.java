package io.github.fourlastor.game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.FollowBodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.MovingComponent;
import io.github.fourlastor.game.level.component.PlayerRequestComponent;
import io.github.fourlastor.game.level.component.SpikeComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.input.controls.Controls;
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SensorBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

/**
 * Factory to create various entities: player, buildings, enemies..
 */
@ScreenScoped
@SuppressWarnings("DataFlowIssue")
public class EntitiesFactory {

    private static final int TILE_SIZE = 16;
    private static final Comparator<Rectangle> RECTANGLE_COMPARATOR = (a, b) -> (int) (a.y - b.y);
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

        List<LdtkLayerInstance> layerInstances =
                definition.layerInstances == null ? new ArrayList<>() : definition.layerInstances;
        for (LdtkLayerInstance layerInstance : layerInstances) {
            boolean[][] tiles = new boolean[layerInstance.cWid][layerInstance.cHei];
            if (!"AutoLayer".equals(layerInstance.type)) {
                continue;
            }
            LdtkTilesetDefinition tileset = definitions.tileset(layerInstance.tilesetDefUid);
            for (LdtkTileInstance tileInstance : layerInstance.autoLayerTiles) {
                tiles[tileInstance.x() / TILE_SIZE][
                        tileInstance.y(layerInstance.cHei, layerInstance.gridSize) / TILE_SIZE] = true;
                Image tile = parser.tile(layerInstance, tileset, tileInstance);
                Entity entity = new Entity();
                entity.add(new ActorComponent(tile, ActorComponent.Layer.BG_PARALLAX));
                entities.add(entity);
            }
            ArrayList<Rectangle> rectangles = new ArrayList<>();
            for (int x = 0; x < layerInstance.cWid; x++) {
                int startY = -1;
                int endY = -1;
                for (int y = 0; y < layerInstance.cHei; y++) {
                    if (tiles[x][y]) {
                        if (startY < 0) {
                            startY = y;
                        }
                        endY = y;
                    } else if (startY >= 0) {
                        ArrayList<Rectangle> overlaps = new ArrayList<>();
                        for (Rectangle r : rectangles) {
                            if (r.width == x - 1 && startY <= r.y && endY >= r.height) {
                                overlaps.add(r);
                            }
                        }
                        overlaps.sort(RECTANGLE_COMPARATOR);
                        for (Rectangle r : overlaps) {
                            if (startY < r.y) {
                                rectangles.add(new Rectangle(x, startY, x, r.y - 1));
                                startY = (int) r.y;
                            }
                            if (startY == r.y) {
                                r.width += 1;
                                if (endY == r.height) {
                                    startY = -1;
                                    endY = -1;
                                } else if (endY > r.height) {
                                    startY = (int) (r.height + 1);
                                }
                            }
                        }
                        if (startY >= 0) {
                            rectangles.add(new Rectangle(x, startY, x, endY));
                            startY = -1;
                            endY = -1;
                        }
                    }
                }
            }
            for (Rectangle rectangle : rectangles) {
                Entity entity = new Entity();
                entity.add(new SolidBodyComponent());
                float width = rectangle.width - rectangle.x + 1;
                float height = rectangle.height - rectangle.y + 1;
                Rectangle area = new Rectangle(
                        rectangle.x * TILE_SIZE, rectangle.y * TILE_SIZE, width * TILE_SIZE, height * TILE_SIZE);
                entity.add(new TransformComponent(new Transform(area)));
                entities.add(entity);
            }
        }
        return entities;
    }

    public Entity character() {
        Entity entity = new Entity();
        LdtkLayerInstance entityLayer = entityLayer();
        LdtkEntityInstance playerSpawn = playerSpawn(entityLayer);
        AnimationNode.Group animationNode =
                assetManager.get("images/included/animations/character/character.json", AnimationNode.Group.class);
        AnimationStateMachine animation = new AnimationStateMachine(animationNode);
        float scale = config.display.scale;
        animation.setOrigin(Align.left);
        float halfScale = scale / 2;
        animation.setScale(halfScale, halfScale);
        entity.add(new ActorComponent(animation, ActorComponent.Layer.CHARACTER));
        entity.add(new FollowBodyComponent());
        entity.add(new KinematicBodyComponent());
        entity.add(new TransformComponent(new Transform(new Rectangle(
                playerSpawn.x(),
                playerSpawn.y(entityLayer.cHei, entityLayer.gridSize),
                playerSpawn.width / 2f,
                playerSpawn.height))));
        entity.add(new GravityComponent(new Vector2(config.physics.gravity)));
        entity.add(new MovingBodyComponent());
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
                entity.add(new SensorBodyComponent());
                entity.add(
                        new TransformComponent(new Transform(new Rectangle(x, y, instance.width / 2f, instance.height)
                                .setCenter(x + instance.width / 2f, y + instance.height / 2f))));
                entity.add(new SpikeComponent());
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
            if ("Timed_floor".equals(instance.identifier)) {
                Entity entity = new Entity();
                String tileName = instance.identifier.toLowerCase(Locale.ROOT).replace('_', '-');
                Image actor = new Image(atlas.findRegion("entities/" + tileName));
                actor.setScale(scale);
                float x = instance.x();
                float y = instance.y(entityLayer.cHei, entityLayer.gridSize);
                actor.setPosition(x * scale, y * scale);
                entity.add(new ActorComponent(actor, ActorComponent.Layer.PLATFORM));
                entity.add(new SolidBodyComponent());
                entity.add(new TransformComponent(
                        new Transform(new Rectangle(x, y, instance.width, instance.height * 3f / 16f)
                                .setCenter(x + instance.width / 2f, y + instance.height / 2f))));
                boolean enabled = instance.field("Colliding").booleanValue;
                float period = instance.field("Period").floatValue;
                entity.add(new TimedFloorComponent.Request(enabled, period));
                entities.add(entity);
            }
        }
        return entities;
    }

    public List<Entity> movingFloors() {
        ArrayList<Entity> entities = new ArrayList<>();
        LdtkLayerInstance entityLayer = entityLayer();
        for (LdtkEntityInstance instance : entityLayer.entityInstances) {
            if ("Moving_floor".equals(instance.identifier)) {
                Entity entity = new Entity();
                Image image = new Image(atlas.findRegion("whitePixel"));
                image.setSize(instance.width, instance.height);
                entity.add(new FollowBodyComponent());
                entity.add(new ActorComponent(image, ActorComponent.Layer.BG_PARALLAX));
                entity.add(new TransformComponent(new Transform(new Rectangle(
                        instance.x(),
                        instance.y(entityLayer.cHei, entityLayer.gridSize),
                        instance.width,
                        instance.height))));
                entity.add(new MovingBodyComponent());
                entity.add(new SolidBodyComponent());

                float speed = instance.field("Speed").floatValue;
                List<Vector2> arrayValue = instance.field("Path").vectorArrayValue;
                List<Vector2> path = new ArrayList<>(arrayValue.size());
                for (Vector2 vector2 : arrayValue) {
                    float y = (entityLayer.cHei - 1) - vector2.y;
                    path.add(new Vector2(vector2.x, y).scl(entityLayer.gridSize));
                }
                entity.add(new MovingComponent(path, speed * entityLayer.gridSize));
                entities.add(entity);
            }
        }
        return entities;
    }
}
