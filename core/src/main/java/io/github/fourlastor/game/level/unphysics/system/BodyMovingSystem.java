package io.github.fourlastor.game.level.unphysics.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import javax.inject.Inject;

/**
 * @see <a href=https://maddythorson.medium.com/celeste-and-towerfall-physics-d24bd2ae0fc5>Tutorial by Maddy Thorson</a>
 */
public class BodyMovingSystem extends IntervalSystem {

    private static final Family FAMILY_SOLID_IMMOBILE =
            Family.all(SolidBodyComponent.class).exclude(MovingBodyComponent.class).get();
    private static final Family FAMILY_SOLID_MOVING =
            Family.all(MovingBodyComponent.class, SolidBodyComponent.class).get();
    private static final Family FAMILY_KINEMATIC =
            Family.all(MovingBodyComponent.class, KinematicBodyComponent.class).get();
    private static final float INTERVAL = 1f / 60f;
    private static final float CHUNK_SIZE = 16 * 5f;

    private final ComponentMapper<TransformComponent> transforms;
    private final ComponentMapper<MovingBodyComponent> movingBodies;
    private final ComponentMapper<SolidBodyComponent> solidBodies;
    private final ComponentMapper<KinematicBodyComponent> kinematicBodies;
    private ImmutableArray<Entity> solidMovingEntities;
    private ImmutableArray<Entity> kinematicEntities;
    private IntMap<Array<Entity>> immobileSolids;

    @Inject
    public BodyMovingSystem(
            ComponentMapper<TransformComponent> transforms,
            ComponentMapper<MovingBodyComponent> movingBodies,
            ComponentMapper<SolidBodyComponent> solidBodies,
            ComponentMapper<KinematicBodyComponent> kinematicBodies) {
        super(INTERVAL);
        this.transforms = transforms;
        this.movingBodies = movingBodies;
        this.solidBodies = solidBodies;
        this.kinematicBodies = kinematicBodies;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(FAMILY_SOLID_IMMOBILE, solidChunkListener);
        immobileSolids = new IntMap<>();
        solidMovingEntities = engine.getEntitiesFor(FAMILY_SOLID_MOVING);
        kinematicEntities = engine.getEntitiesFor(FAMILY_KINEMATIC);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(solidChunkListener);
        immobileSolids = null;
        solidMovingEntities = null;
        kinematicEntities = null;
        super.removedFromEngine(engine);
    }

    @Override
    protected void updateInterval() {
        for (Entity entity : kinematicEntities) {
            moveKinematic(entity, INTERVAL);
        }
        for (Entity entity : solidMovingEntities) {
            moveSolid(entity, INTERVAL);
        }
    }

    private void moveSolid(Entity entity, float delta) {
        MovingBodyComponent movingBody = movingBodies.get(entity);
        SolidBodyComponent solidBody = solidBodies.get(entity);
        Transform transform = transforms.get(entity).transform;
        float x = movingBody.speed.x * delta;
        float y = movingBody.speed.y * delta;
        movingBody.xRemainder += x;
        movingBody.yRemainder += y;
        int moveX = (int) movingBody.xRemainder;
        int moveY = (int) movingBody.yRemainder;
        if (moveX != 0 || moveY != 0) {
            solidBody.canCollide = false;
            if (moveX != 0) {
                movingBody.xRemainder -= moveX;
                transform.moveXBy(moveX);
                checkSolidCollisionsX(solidBody, transform, moveX);
            }
            if (moveY != 0) {
                movingBody.yRemainder -= moveY;
                transform.moveYBy(moveY);
                checkSolidCollisionsY(solidBody, transform, moveY);
            }
            // Re-enable collisions for this Solid
            solidBody.canCollide = true;
        }
    }

    public void checkSolidCollisionsX(SolidBodyComponent solidBody, Transform solidTransform, float moveX) {
        for (Entity entity : kinematicEntities) {
            KinematicBodyComponent kinematicBody = kinematicBodies.get(entity);
            Transform kinematicTransform = transforms.get(entity).transform;
            if (moveX > 0) {
                if (collides(kinematicTransform.area(), solidBody, solidTransform)) {
                    moveKinematicX(
                            solidTransform.right() - kinematicTransform.left(),
                            kinematicBody,
                            kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicX(moveX, kinematicBody, kinematicTransform);
                }
            } else {
                if (collides(kinematicTransform.area(), solidBody, solidTransform)) {
                    moveKinematicX(
                            solidTransform.left() - kinematicTransform.right(),
                            kinematicBody,
                            kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicX(moveX, kinematicBody, kinematicTransform);
                }
            }
        }
    }

    public void checkSolidCollisionsY(SolidBodyComponent solidBody, Transform solidTransform, float moveY) {
        for (Entity entity : kinematicEntities) {
            KinematicBodyComponent kinematicBody = kinematicBodies.get(entity);
            MovingBodyComponent movingBody = movingBodies.get(entity);
            Transform kinematicTransform = transforms.get(entity).transform;
            if (moveY > 0) {
                if (collides(kinematicTransform.area(), solidBody, solidTransform)) {
                    moveKinematicY(
                            solidTransform.top() - kinematicTransform.bottom(),
                            kinematicBody,
                            movingBody, kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicY(moveY, kinematicBody, movingBody, kinematicTransform);
                }
            } else {
                if (collides(kinematicTransform.area(), solidBody, solidTransform)) {
                    moveKinematicY(
                            solidTransform.bottom() - kinematicTransform.top(),
                            kinematicBody,
                            movingBody, kinematicTransform);
                } else if (isRiding(kinematicTransform, solidTransform)) {
                    moveKinematicY(moveY, kinematicBody, movingBody, kinematicTransform);
                }
            }
        }
    }

    private void moveKinematic(Entity entity, float delta) {
        MovingBodyComponent movingBody = movingBodies.get(entity);
        KinematicBodyComponent kinematicBody = kinematicBodies.get(entity);
        Transform kinematicTransform = transforms.get(entity).transform;
        resetCollisions(kinematicBody);
        moveKinematicX(delta * movingBody.speed.x, kinematicBody, kinematicTransform);
        moveKinematicY(delta * movingBody.speed.y, kinematicBody, movingBody, kinematicTransform);
    }

    private void resetCollisions(KinematicBodyComponent kinematicBody) {
        kinematicBody.collision.set(0, 0);
    }

    public void moveKinematicX(
            float amount, KinematicBodyComponent kinematicBody, Transform transform) {
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
            return false;
        }
    }

    private boolean attemptMoveY(KinematicBodyComponent kinematicBody, MovingBodyComponent movingBody, Transform transform, float amount) {
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
            return false;
        }
    }

    private final Rectangle tmp = new Rectangle();

    private Rectangle offsetBy(Rectangle area, float x, float y) {
        Rectangle tmp = this.tmp.set(area);
        return tmp.setPosition(tmp.x + x, tmp.y + y);
    }

    private boolean collides(Rectangle area) {
        int startX = (int) (area.x / CHUNK_SIZE);
        int endX = (int) ((area.x + area.width) / CHUNK_SIZE);
        int startY = (int) (area.y / CHUNK_SIZE);
        int endY = (int) ((area.y + area.height) / CHUNK_SIZE);
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                int fused = fusedCoordinates(x, y);
                Array<Entity> entities = immobileSolids.get(fused);
                if (entities == null) continue;
                for (Entity entity : entities) {
                    if (collides(area, solidBodies.get(entity), transforms.get(entity).transform)) {
                        return true;
                    }
                }
            }
        }
        for (Entity entity : solidMovingEntities) {
            if (collides(area, solidBodies.get(entity), transforms.get(entity).transform)) {
                return true;
            }
        }
        return false;
    }

    private boolean collides(Rectangle area, SolidBodyComponent body, Transform solidTransform) {
        return body.canCollide && solidTransform.area().overlaps(area);
    }

    private boolean isRiding(Transform kinematicTransform, Transform solidTransform) {
        Rectangle kinematicArea = kinematicTransform.area();
        Rectangle solidArea = solidTransform.area();
        return kinematicArea.overlaps(solidArea)
                || offsetBy(kinematicArea, 0, 3).overlaps(solidArea);
    }


    private final EntityListener solidChunkListener = new EntityListener() {
        @Override
        public void entityAdded(Entity entity) {
            Transform area = transforms.get(entity).transform;
            int startX = (int) (area.left() / CHUNK_SIZE);
            int endX = (int) (area.right() / CHUNK_SIZE);
            int startY = (int) (area.bottom() / CHUNK_SIZE);
            int endY = (int) (area.top() / CHUNK_SIZE);
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    int fused = fusedCoordinates(x, y);
                    if (!immobileSolids.containsKey(fused)) {
                        immobileSolids.put(fused, new Array<>());
                    }
                    immobileSolids.get(fused).add(entity);
                }
            }
        }

        @Override
        public void entityRemoved(Entity entity) {
            Transform area = transforms.get(entity).transform;
            int startX = (int) (area.left() / CHUNK_SIZE);
            int endX = (int) (area.right() / CHUNK_SIZE);
            int startY = (int) (area.bottom() / CHUNK_SIZE);
            int endY = (int) (area.top() / CHUNK_SIZE);
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    int fused = fusedCoordinates(x, y);
                    if (!immobileSolids.containsKey(fused)) {
                        continue;
                    }
                    immobileSolids.get(fused).removeValue(entity, true);
                }
            }
        }
    };

    private int fusedCoordinates(int x, int y) {
        return x << 16 | (y & 0xFFFF);
    }
}
