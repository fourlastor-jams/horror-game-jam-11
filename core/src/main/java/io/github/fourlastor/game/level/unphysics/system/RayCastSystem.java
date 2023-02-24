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
import io.github.fourlastor.game.level.unphysics.Transform;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SensorBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import javax.inject.Inject;

/**
 * @see <a href="https://github.com/OneLoneCoder/Javidx9/blob/master/PixelGameEngine/SmallerProjects/OneLoneCoder_PGE_Rectangles.cpp">Tutorial by Maddy Thorson</a>
 */
public class RayCastSystem extends EntitySystem {

    private static final Family FAMILY_SOLID =
            Family.all(SolidBodyComponent.class, TransformComponent.class).get();
    private static final Family FAMILY_SOLID_MOVING = Family.all(
                    SolidBodyComponent.class, TransformComponent.class, MovingBodyComponent.class)
            .get();
    private static final Family FAMILY_SENSOR =
            Family.all(SensorBodyComponent.class, TransformComponent.class).get();
    private static final Family FAMILY_KINEMATIC =
            Family.all(MovingBodyComponent.class, KinematicBodyComponent.class).get();
    private static final float CHUNK_SIZE = 16 * 5f;

    private final ComponentMapper<TransformComponent> transforms;
    private final ComponentMapper<MovingBodyComponent> movingBodies;
    private final ComponentMapper<SolidBodyComponent> solidBodies;
    private final ComponentMapper<SensorBodyComponent> sensorBodies;
    private final ComponentMapper<KinematicBodyComponent> kinematicBodies;
    private ImmutableArray<Entity> kinematicEntities;
    private ImmutableArray<Entity> sensorEntities;
    private ImmutableArray<Entity> solidMovingEntities;
    private final PriorityQueue<Contact> contacts = new PriorityQueue<>((o1, o2) -> Float.compare(o1.t, o2.t));
    private final Rectangle expandedTarget = new Rectangle();

    @Inject
    public RayCastSystem(
            ComponentMapper<TransformComponent> transforms,
            ComponentMapper<MovingBodyComponent> movingBodies,
            ComponentMapper<SolidBodyComponent> solidBodies,
            ComponentMapper<SensorBodyComponent> sensorBodies,
            ComponentMapper<KinematicBodyComponent> kinematicBodies) {
        this.transforms = transforms;
        this.movingBodies = movingBodies;
        this.solidBodies = solidBodies;
        this.sensorBodies = sensorBodies;
        this.kinematicBodies = kinematicBodies;
    }

    private final List<Entity> solidEntities = new ArrayList<>();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(FAMILY_SOLID, solidEntitiesListener);
        kinematicEntities = engine.getEntitiesFor(FAMILY_KINEMATIC);
        sensorEntities = engine.getEntitiesFor(FAMILY_SENSOR);
        solidMovingEntities = engine.getEntitiesFor(FAMILY_SOLID_MOVING);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(solidEntitiesListener);
        solidEntities.clear();
        kinematicEntities = null;
        sensorEntities = null;
        super.removedFromEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : solidMovingEntities) {
            moveSolid(entity, deltaTime);
        }
        for (Entity entity : kinematicEntities) {
            resetCollisions(kinematicBodies.get(entity));
            moveKinematic(entity, deltaTime, movingBodies.get(entity).speed);
        }
    }

    private final Vector2 movingSpeedFromSolid = new Vector2();
    private final Contact movingSolidContact = new Contact();

    private void moveSolid(Entity entity, float delta) {
        MovingBodyComponent movingBody = movingBodies.get(entity);
        SolidBodyComponent solidBody = solidBodies.get(entity);
        Transform transform = transforms.get(entity).transform;
        float moveX = movingBody.speed.x * delta;
        float moveY = movingBody.speed.y * delta;
        if (moveX != 0 || moveY != 0) {
            solidBody.canCollide = false;
            for (Entity kinematicEntity : kinematicEntities) {
                Transform kinematicTransform = transforms.get(kinematicEntity).transform;
                if (isPushing(transform, kinematicTransform, movingBody.speed)) {
                    resolveDynamicRectVsRect(
                            transform.area(), movingBody.speed, delta, kinematicTransform.area(), movingSolidContact);
                    float amountX;
                    float amountY;
                    if (movingSolidContact.normal.x != 0) {
                        amountX = moveX > 0
                                ? transform.right() - kinematicTransform.left()
                                : transform.left() - kinematicTransform.right();
                        amountY = 0;
                    } else {
                        amountX = 0;
                        amountY = moveY > 0
                                ? transform.top() - kinematicTransform.bottom()
                                : transform.bottom() - kinematicTransform.top();
                    }
                    moveKinematic(kinematicEntity, delta, movingSpeedFromSolid.set(amountX / delta, amountY / delta));
                } else if (isRiding(kinematicTransform, transform)) {
                    moveKinematic(kinematicEntity, delta, movingSpeedFromSolid.set(moveX / delta, moveY / delta));
                }
            }
            if (moveX != 0) {
                transform.moveXBy(moveX);
            }
            if (moveY != 0) {
                transform.moveYBy(moveY);
            }
            // Re-enable collisions for this Solid
            solidBody.canCollide = true;
        }
    }

    private final Vector2 solidCenterTmp = new Vector2();
    private final Vector2 kinematicCenterTmp = new Vector2();

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

    private boolean isRiding(Transform kinematicTransform, Transform solidTransform) {
        Rectangle kinematicArea = kinematicTransform.area();
        Rectangle solidArea = solidTransform.area();
        return kinematicArea.overlaps(solidArea)
                || offsetBy(kinematicArea, 0, -1).overlaps(solidArea);
    }

    private void moveKinematic(Entity entity, float delta, Vector2 speed) {
        KinematicBodyComponent kinematicBody = kinematicBodies.get(entity);
        Transform inputTransform = transforms.get(entity).transform;
        Rectangle inputRect = inputTransform.area();
        contacts.clear();
        for (int i = 0; i < solidEntities.size(); i++) {
            Entity solidEntity = solidEntities.get(i);
            Rectangle targetRect = transforms.get(solidEntity).transform.area();
            if (!solidBodies.get(solidEntity).canCollide) {
                continue;
            }
            Contact contact = new Contact();
            contact.id = i;
            if (dynamicRectVsRect(inputRect, speed, delta, targetRect, contact)) {
                contacts.add(contact);
            }
        }
        if (contacts.isEmpty()) {
            kinematicBody.touching.set(0, 0);
        }
        Contact contact = contacts.poll();
        while (contact != null) {
            Entity solidEntity = solidEntities.get(contact.id);
            Rectangle targetRect = transforms.get(solidEntity).transform.area();
            if (resolveDynamicRectVsRect(inputRect, speed, delta, targetRect, contact)) {
                kinematicBody.touching.set(
                        contact.normal.x != 0 ? (int) contact.normal.x : kinematicBody.touching.x,
                        contact.normal.y != 0 ? (int) contact.normal.y : kinematicBody.touching.y);
            }
            contact = contacts.poll();
        }

        float dX = delta * speed.x;
        float dY = delta * speed.y;
        inputTransform.moveXBy(dX);
        inputTransform.moveYBy(dY);
        checkSensors(kinematicBody, inputRect);
    }

    private void resetCollisions(KinematicBodyComponent kinematicBody) {
        kinematicBody.collision.set(0, 0);
        if (!kinematicBody.sensors.isEmpty()) {
            kinematicBody.sensors.clear();
        }
    }

    private final Rectangle tmp = new Rectangle();

    private Rectangle offsetBy(Rectangle area, float x, float y) {
        Rectangle tmp = this.tmp.set(area);
        return tmp.setPosition(tmp.x + x, tmp.y + y);
    }

    private void checkSensors(KinematicBodyComponent bodyComponent, Rectangle area) {
        for (Entity entity : sensorEntities) {
            if (checkSensor(area, sensorBodies.get(entity), transforms.get(entity).transform)) {
                bodyComponent.sensors.add(entity);
            }
        }
    }

    private boolean checkSensor(Rectangle area, SensorBodyComponent sensorBody, Transform transform) {
        return sensorBody.canCollide && transform.area().overlaps(area);
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

    private final Vector2 invDir = new Vector2();
    private final Vector2 tNear = new Vector2();
    private final Vector2 tFar = new Vector2();
    private final Vector2 tFarSize = new Vector2();
    private final Vector2 dynamicCenter = new Vector2();
    private final Vector2 velocityDelta = new Vector2();
    private final Vector2 scaledVelocity = new Vector2();

    private boolean rayVsRect(Vector2 rayOrigin, Vector2 rayDir, Rectangle target, Contact contact) {
        contact.normal.set(Vector2.Zero);
        invDir.set(1f / rayDir.x, 1f / rayDir.y);

        // Calculate intersections with rectangle bounding axes
        target.getPosition(tNear).sub(rayOrigin).scl(invDir);
        target.getPosition(tFar).add(target.getSize(tFarSize)).sub(rayOrigin).scl(invDir);
        if (Float.isNaN(tNear.x) || Float.isNaN(tNear.y) || Float.isNaN(tFar.x) || Float.isNaN(tFar.y)) {
            return false;
        }

        // Sort distances
        if (tNear.x > tFar.x) {
            float tmp = tNear.x;
            tNear.x = tFar.x;
            tFar.x = tmp;
        }
        if (tNear.y > tFar.y) {
            float tmp = tNear.y;
            tNear.y = tFar.y;
            tFar.y = tmp;
        }

        // Early rejection
        if (tNear.x > tFar.y || tNear.y > tFar.x) {
            return false;
        }

        // Closest 'time' will be the first contact
        float tHitNear = Math.max(tNear.x, tNear.y);
        // Furthest 'time' is contact on opposite side of target
        float tHitFar = Math.min(tFar.x, tFar.y);
        // Reject if ray direction is pointing away from object
        if (tHitFar < 0) {
            return false;
        }

        contact.t = tHitNear;

        // Contact point of collision from parametric line equation
        contact.point.set(rayDir).scl(tHitNear).add(rayOrigin);

        if (tNear.x > tNear.y) {
            contact.normal.set(-Math.signum(invDir.x), 0);
        } else if (tNear.x < tNear.y) {
            contact.normal.set(0, -Math.signum(invDir.y));
        }
        // Note if t_near == t_far, collision is principle in a diagonal
        // so pointless to resolve. By returning a CN={0,0} even though its
        // considered a hit, the resolver won't change anything.
        return true;
    }

    private boolean dynamicRectVsRect(
            Rectangle dynamicRect, Vector2 velocity, float timeStep, Rectangle staticRect, Contact contact) {
        // Check if dynamic rectangle is actually moving - we assume rectangles are NOT in collision to start
        if (velocity.x == 0 && velocity.y == 0) {
            return false;
        }

        expandedTarget.set(
                staticRect.x - dynamicRect.width / 2f,
                staticRect.y - dynamicRect.height / 2f,
                staticRect.width + dynamicRect.width,
                staticRect.height + dynamicRect.height);

        if (rayVsRect(
                dynamicRect.getCenter(dynamicCenter),
                scaledVelocity.set(velocity).scl(timeStep),
                expandedTarget,
                contact)) {
            return contact.t >= -0.1f && contact.t < 1f;
        } else {
            return false;
        }
    }

    private boolean resolveDynamicRectVsRect(
            Rectangle dynamicRect, Vector2 velocity, float timeStep, Rectangle staticRect, Contact contact) {
//        Rectangle[] contacts = new Rectangle[4];
        if (dynamicRectVsRect(dynamicRect, velocity, timeStep, staticRect, contact)) {
//            contacts[0] = contact.normal.y > 0 ? staticRect : null;
//            contacts[1] = contact.normal.x < 0 ? staticRect : null;
//            contacts[2] = contact.normal.y < 0 ? staticRect : null;
//            contacts[3] = contact.normal.x > 0 ? staticRect : null;
            // reduce speed to ensure there is no contact
            velocity.add(velocityDelta
                    .set(Math.abs(velocity.x), Math.abs(velocity.y))
                    .scl(contact.normal)
                    .scl(1 - contact.t));
            return true;
        }

        return false;
    }

    private static class Contact {
        public final Vector2 normal = new Vector2();
        public final Vector2 point = new Vector2();
        public int id;
        public float t;

        public void reset() {
            normal.set(Vector2.Zero);
            point.set(Vector2.Zero);
            id = -1;
            t = 0;
        }
    }

    private final EntityListener solidEntitiesListener = new EntityListener() {
        @Override
        public void entityAdded(Entity entity) {
            solidEntities.add(entity);
        }

        @Override
        public void entityRemoved(Entity entity) {
            solidEntities.remove(entity);
        }
    };

    private long fusedCoordinates(int x, int y) {
        return (long) x << 32 | (y & 0xFFFFFFFFL);
    }
}
