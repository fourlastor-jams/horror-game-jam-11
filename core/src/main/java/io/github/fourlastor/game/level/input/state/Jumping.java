package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;

import javax.inject.Inject;

public class Jumping extends HorizontalMovement {

    private final GameConfig config;

    private float initialY;

    @Inject
    public Jumping(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs,
            GameConfig config) {
        super(players, bodies, animated, inputs, config);
        this.config = config;
    }

    @Override
    protected String animation() {
        return "jump";
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        Body body = bodies.get(entity).body;
        setVerticalVelocity(body, calculateVerticalVelocityForHeight(config.player.maxJumpHeight));
        initialY = body.getPosition().y;
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Body body = bodies.get(entity).body;
        body.applyForceToCenter(config.player.antiGravity, false);
        float distanceTravelled = Math.abs(body.getPosition().y - initialY);
        if (config.player.minJumpHeight <= distanceTravelled && !inputs.get(entity).jumpPressed) {
            setVerticalVelocity(body, body.getLinearVelocity().y / 1.7f);
        }
        if (body.getLinearVelocity().y <= 0f) {
            PlayerComponent playerComponent = players.get(entity);
            playerComponent.stateMachine.changeState(playerComponent.fallingFromJump);
        }
    }

    private void setVerticalVelocity(Body body, float vY) {
        body.setLinearVelocity(body.getLinearVelocity().x, vY);
    }

    /** @see <a href="http://www.iforce2d.net/b2dtut/projected-trajectory">Box2D projected trajectory</a> */
    float calculateVerticalVelocityForHeight(float desiredHeight) {
        if (desiredHeight <= 0) return 0;

        // gravity is given per second, but we want time step values here
        float t = 1 / 60.0f;
        float stepGravity = (config.physics.gravity.y + config.player.antiGravity.y) * t * t; // m/s/s

        // quadratic equation setup (ax² + bx + c = 0)
        float a = 0.5f / stepGravity;
        float b = 0.5f;
        @SuppressWarnings("UnnecessaryLocalVariable") // names
        float c = desiredHeight;

        float quadraticSolution = Math.abs((float) ((-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a)));

        // convert answer back to seconds
        return quadraticSolution * 60.0f;
    }
}
