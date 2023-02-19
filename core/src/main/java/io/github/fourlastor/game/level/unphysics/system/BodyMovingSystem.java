package io.github.fourlastor.game.level.unphysics.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Null;
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SensorBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

/**
 * @see <a href=https://maddythorson.medium.com/celeste-and-towerfall-physics-d24bd2ae0fc5>Tutorial by Maddy Thorson</a>
 */
public class BodyMovingSystem extends EntitySystem {

    private static final Family FAMILY_IMMOBILE = Family.one(SolidBodyComponent.class, SensorBodyComponent.class)
            .exclude(MovingBodyComponent.class)
            .get();
    private static final Family FAMILY_SOLID_MOVING =
            Family.all(MovingBodyComponent.class, SolidBodyComponent.class).get();
    private static final Family FAMILY_KINEMATIC =
            Family.all(MovingBodyComponent.class, KinematicBodyComponent.class).get();
    private static final float CHUNK_SIZE = 16 * 5f;
    private static final float STEP = 1f / 60f;

    private final ComponentMapper<TransformComponent> transforms;
    private final ComponentMapper<MovingBodyComponent> movingBodies;
    private final ComponentMapper<SolidBodyComponent> solidBodies;
    private final ComponentMapper<SensorBodyComponent> sensorBodies;
    private final ComponentMapper<KinematicBodyComponent> kinematicBodies;
    private final ComponentMapper<GravityComponent> gravities;
    private ImmutableArray<Entity> solidMovingEntities;
    private Array<Entity> solidMovingEntitiesCollisions;
    private ImmutableArray<Entity> kinematicEntities;
    private LongMap<Array<Entity>> immobileSolids;
    private LongMap<Array<Entity>> immobileSensors;

    @Inject
    public BodyMovingSystem(
            ComponentMapper<TransformComponent> transforms,
            ComponentMapper<MovingBodyComponent> movingBodies,
            ComponentMapper<SolidBodyComponent> solidBodies,
            ComponentMapper<SensorBodyComponent> sensorBodies,
            ComponentMapper<KinematicBodyComponent> kinematicBodies,
            ComponentMapper<GravityComponent> gravities) {
        this.transforms = transforms;
        this.movingBodies = movingBodies;
        this.solidBodies = solidBodies;
        this.sensorBodies = sensorBodies;
        this.kinematicBodies = kinematicBodies;
        this.gravities = gravities;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(FAMILY_IMMOBILE, chunkMappingListener);
        immobileSolids = new LongMap<>();
        immobileSensors = new LongMap<>();
        solidMovingEntities = engine.getEntitiesFor(FAMILY_SOLID_MOVING);
        solidMovingEntitiesCollisions =
                new Array<>(false, solidMovingEntities.toArray(Entity.class), 0, solidMovingEntities.size());
        engine.addEntityListener(FAMILY_SOLID_MOVING, new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                solidMovingEntitiesCollisions.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                solidMovingEntitiesCollisions.removeValue(entity, true);
            }
        });
        kinematicEntities = engine.getEntitiesFor(FAMILY_KINEMATIC);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(chunkMappingListener);
        immobileSolids = null;
        immobileSensors = null;
        solidMovingEntities = null;
        solidMovingEntitiesCollisions = null;
        kinematicEntities = null;
        super.removedFromEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        while (deltaTime > 0f) {
            for (Entity entity : kinematicEntities) {
                moveKinematic(entity, deltaTime);
            }
            for (Entity entity : solidMovingEntities) {
                moveSolid(entity, deltaTime);
            }
            deltaTime -= STEP;
        }
    }

    private void moveSolid(Entity entity, float delta) {
        MovingBodyComponent movingBody = movingBodies.get(entity);
        SolidBodyComponent solidBody = solidBodies.get(entity);
        Transform transform = transforms.get(entity).transform;
        float moveX = movingBody.speed.x * delta;
        float moveY = movingBody.speed.y * delta;
        if (moveX != 0 || moveY != 0) {
            solidBody.canCollide = false;
            if (moveX != 0) {
                transform.moveXBy(moveX);
                checkSolidCollisionsX(transform, moveX);
            }
            if (moveY != 0) {
                transform.moveYBy(moveY);
                checkSolidCollisionsY(transform, moveY);
            }
            // Re-enable collisions for this Solid
            solidBody.canCollide = true;
        }
    }

    public void checkSolidCollisionsX(Transform solidTransform, float moveX) {
        for (Entity entity : kinematicEntities) {
            KinematicBodyComponent kinematicBody = kinematicBodies.get(entity);
            Transform kinematicTransform = transforms.get(entity).transform;
            if (moveX > 0) {
                if (isPushing(solidTransform, kinematicTransform, pushedTmp.set(moveX, 0))) {
                    moveKinematicX(
                            solidTransform.right() - kinematicTransform.left(), kinematicBody, kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicX(moveX, kinematicBody, kinematicTransform);
                }
            } else {
                if (isPushing(solidTransform, kinematicTransform, pushedTmp.set(moveX, 0))) {
                    moveKinematicX(
                            solidTransform.left() - kinematicTransform.right(), kinematicBody, kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicX(moveX, kinematicBody, kinematicTransform);
                }
            }
        }
    }

    public void checkSolidCollisionsY(Transform solidTransform, float moveY) {
        for (Entity entity : kinematicEntities) {
            KinematicBodyComponent kinematicBody = kinematicBodies.get(entity);
            MovingBodyComponent movingBody = movingBodies.get(entity);
            Transform kinematicTransform = transforms.get(entity).transform;
            if (moveY > 0) {
                if (isPushing(solidTransform, kinematicTransform, pushedTmp.set(0, moveY))) {
                    moveKinematicY(
                            solidTransform.top() - kinematicTransform.bottom(),
                            kinematicBody,
                            movingBody,
                            kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicY(moveY, kinematicBody, movingBody, kinematicTransform);
                }
            } else {
                if (isPushing(solidTransform, kinematicTransform, pushedTmp.set(0, moveY))) {
                    moveKinematicY(
                            solidTransform.bottom() - kinematicTransform.top(),
                            kinematicBody,
                            movingBody,
                            kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicY(moveY, kinematicBody, movingBody, kinematicTransform);
                }
            }
        }
    }

    private final Vector2 pushedTmp = new Vector2();
    private final Vector2 kinematicCenterTmp = new Vector2();

    private final Vector2 solidCenterTmp = new Vector2();

    private boolean isPushing(Transform solidTransform, Transform kinematicTransform, Vector2 direction) {
        Rectangle solidArea = solidTransform.area();
        Rectangle kinematicArea = kinematicTransform.area();
        if (!solidArea.overlaps(kinematicArea)) {
            return false;
        }

        Vector2 solidCenter = solidArea.getCenter(solidCenterTmp);
        Vector2 bodyDirection = kinematicArea.getCenter(kinematicCenterTmp).sub(solidCenter);
        if (direction.x != 0) {
            return Math.abs(bodyDirection.x) > Math.abs(bodyDirection.y) && bodyDirection.x * direction.x > 0;
        } else {
            return Math.abs(bodyDirection.y) > Math.abs(bodyDirection.x) && bodyDirection.y * direction.y > 0;
        }
    }

    private void moveKinematic(Entity entity, float delta) {
        MovingBodyComponent movingBody = movingBodies.get(entity);
        KinematicBodyComponent kinematicBody = kinematicBodies.get(entity);
        Transform kinematicTransform = transforms.get(entity).transform;
        Vector2 gravity = gravities.get(entity).gravity;
        resetCollisions(kinematicBody);
        float dX = delta * (movingBody.speed.x + delta * gravity.x / 2);
        moveKinematicX(dX, kinematicBody, kinematicTransform);
        float dY = delta * (movingBody.speed.y + delta * gravity.x / 2);
        moveKinematicY(dY, kinematicBody, movingBody, kinematicTransform);
    }

    private void resetCollisions(KinematicBodyComponent kinematicBody) {
        kinematicBody.collision.set(0, 0);
        if (!kinematicBody.sensors.isEmpty()) {
            kinematicBody.sensors.clear();
        }
    }

    public void moveKinematicX(float amount, KinematicBodyComponent kinematicBody, Transform transform) {
        int move = (int) amount;
        int sign = Integer.signum(move);
        if (move != 0) {
            amount -= move;
            for (int i = 0; i < Math.abs(move); i++) {
                if (attemptMoveX(kinematicBody, transform, sign)) {
                    break;
                }
            }
        }
        if (kinematicBody.touching.x == 0) {
            attemptMoveX(kinematicBody, transform, amount);
        }
    }

    public void moveKinematicY(
            float amount, KinematicBodyComponent kinematicBody, MovingBodyComponent movingBody, Transform transform) {
        int move = (int) amount;
        int sign = Integer.signum(move);
        if (move != 0) {
            amount -= move;
            for (int i = 0; i < Math.abs(move); i++) {
                if (attemptMoveY(kinematicBody, movingBody, transform, sign)) {
                    break;
                }
            }
        }
        if (kinematicBody.collision.y == 0) {
            attemptMoveY(kinematicBody, movingBody, transform, amount);
        }
    }

    private boolean attemptMoveX(KinematicBodyComponent kinematicBody, Transform transform, float amount) {
        if (amount == 0) {
            return false;
        }
        float direction = Math.signum(amount);
        if (collides(offsetBy(transform.area(), direction, 0))) {
            // Collision with Solid
            kinematicBody.collision.x = (int) direction;
            kinematicBody.touching.x = (int) direction;
            return true;
        } else {
            // There is no Solid immediately beside us
            kinematicBody.touching.x = 0;
            transform.moveXBy(amount);
            checkSensors(kinematicBody, transform.area());
            return false;
        }
    }

    private boolean attemptMoveY(
            KinematicBodyComponent kinematicBody, MovingBodyComponent movingBody, Transform transform, float amount) {
        if (amount == 0) {
            return false;
        }
        float direction = Math.signum(amount);
        if (collides(offsetBy(transform.area(), 0, direction))) {
            // Collision with Solid
            kinematicBody.collision.y = (int) direction;
            kinematicBody.touching.y = (int) direction;
            movingBody.speed.y = 0f;
            return true;
        } else {
            // There is no Solid immediately beside us
            kinematicBody.touching.y = 0;
            transform.moveYBy(amount);
            checkSensors(kinematicBody, transform.area());
            return false;
        }
    }

    private final Rectangle tmp = new Rectangle();

    private Rectangle offsetBy(Rectangle area, float x, float y) {
        Rectangle tmp = this.tmp.set(area);
        return tmp.setPosition(tmp.x + x, tmp.y + y);
    }

    private void checkSensors(KinematicBodyComponent bodyComponent, Rectangle area) {
        int startX = chunkStartX(area);
        int endX = chunkEndX(area);
        int startY = chunkStartY(area);
        int endY = chunkEndY(area);
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                long fused = fusedCoordinates(x, y);
                Array<Entity> entities = immobileSensors.get(fused);
                if (entities == null) continue;
                for (Entity entity : entities) {
                    if (checkSensor(area, sensorBodies.get(entity), transforms.get(entity).transform)) {
                        bodyComponent.sensors.add(entity);
                    }
                }
            }
        }
    }

    private boolean checkSensor(Rectangle area, SensorBodyComponent sensorBody, Transform transform) {
        return sensorBody.canCollide && transform.area().overlaps(area);
    }

    private boolean collides(Rectangle area) {
        int startX = chunkStartX(area);
        int endX = chunkEndX(area);
        int startY = chunkStartY(area);
        int endY = chunkEndY(area);
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                long fused = fusedCoordinates(x, y);
                Array<Entity> entities = immobileSolids.get(fused);
                if (entities == null) continue;
                for (Entity entity : entities) {
                    if (collides(area, solidBodies.get(entity), transforms.get(entity).transform)) {
                        return true;
                    }
                }
            }
        }
        for (Entity entity : solidMovingEntitiesCollisions) {
            if (collides(area, solidBodies.get(entity), transforms.get(entity).transform)) {
                return true;
            }
        }
        return false;
    }

    private static int chunkStartX(Rectangle area) {
        return (int) (area.x / CHUNK_SIZE);
    }

    private static int chunkEndX(Rectangle area) {
        return (int) ((area.x + area.width) / CHUNK_SIZE);
    }

    private static int chunkStartY(Rectangle area) {
        return (int) (area.y / CHUNK_SIZE);
    }

    private static int chunkEndY(Rectangle area) {
        return (int) ((area.y + area.height) / CHUNK_SIZE);
    }

    private boolean collides(Rectangle area, SolidBodyComponent body, Transform solidTransform) {
        return body.canCollide && solidTransform.area().overlaps(area);
    }

    private boolean isRiding(Transform kinematicTransform, Transform solidTransform) {
        Rectangle kinematicArea = kinematicTransform.area();
        Rectangle solidArea = solidTransform.area();
        return kinematicArea.overlaps(solidArea)
                || offsetBy(kinematicArea, 0, -3).overlaps(solidArea);
    }

    private final EntityListener chunkMappingListener = new EntityListener() {
        @Override
        public void entityAdded(Entity entity) {
            Transform area = transforms.get(entity).transform;
            LongMap<Array<Entity>> immobileMap = mapFor(entity);
            if (immobileMap == null) {
                return;
            }
            int startX = (int) (area.left() / CHUNK_SIZE);
            int endX = (int) (area.right() / CHUNK_SIZE);
            int startY = (int) (area.bottom() / CHUNK_SIZE);
            int endY = (int) (area.top() / CHUNK_SIZE);
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    long fused = fusedCoordinates(x, y);
                    Array<Entity> entities = immobileMap.get(fused);
                    if (entities == null) {
                        entities = new Array<>();
                        immobileMap.put(fused, entities);
                    }
                    entities.add(entity);
                }
            }
        }

        @Override
        public void entityRemoved(Entity entity) {
            Transform area = transforms.get(entity).transform;
            LongMap<Array<Entity>> immobileMap = mapFor(entity);
            if (immobileMap == null) {
                return;
            }
            int startX = (int) (area.left() / CHUNK_SIZE);
            int endX = (int) (area.right() / CHUNK_SIZE);
            int startY = (int) (area.bottom() / CHUNK_SIZE);
            int endY = (int) (area.top() / CHUNK_SIZE);
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    long fused = fusedCoordinates(x, y);
                    Array<Entity> entities = immobileMap.get(fused);
                    if (entities == null) {
                        continue;
                    }
                    entities.removeValue(entity, true);
                }
            }
        }

        @Null
        private LongMap<Array<Entity>> mapFor(Entity entity) {
            if (solidBodies.has(entity)) {
                return immobileSolids;
            } else if (sensorBodies.has(entity)) {
                return immobileSensors;
            } else {
                return null;
            }
        }
    };

    private long fusedCoordinates(int x, int y) {
        return (long) x << 32 | (y & 0xFFFFFFFFL);
    }
}
