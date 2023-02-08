package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import javax.inject.Inject;

public class Jumping extends CharacterState {

    private final GameConfig config;

    @Inject
    public Jumping(
            ComponentMapper<PlayerComponent> players,
            ComponentMapper<BodyComponent> bodies,
            ComponentMapper<AnimatedComponent> animated,
            ComponentMapper<InputComponent> inputs, GameConfig config) {
        super(players, bodies, animated, inputs);
        this.config = config;
    }

    @Override
    protected String animation() {
        return "jump";
    }

    float timePassed = 0f;

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        timePassed = 0f;
        Body body = bodies.get(entity).body;
        body.setLinearVelocity(body.getLinearVelocity().x, calculateVerticalVelocityForHeight(3f));
    }

    @Override
    public void update(Entity entity) {
        timePassed += delta();
        if (timePassed > 1f) {
            PlayerComponent playerComponent = players.get(entity);
            playerComponent.stateMachine.changeState(playerComponent.idle);
        }
    }

    /** @see <a href="http://www.iforce2d.net/b2dtut/projected-trajectory">Box2D projected trajectory</a> */
    float calculateVerticalVelocityForHeight( float desiredHeight )
    {
        if ( desiredHeight <= 0 )
            return 0;

        //gravity is given per second, but we want time step values here
        float t = 1 / 60.0f;
        Vector2 stepGravity = config.gravity.cpy().scl(t * t); // m/s/s

        //quadratic equation setup (ax² + bx + c = 0)
        float a = 0.5f / stepGravity.y;
        float b = 0.5f;
        @SuppressWarnings("UnnecessaryLocalVariable") // names
        float c = desiredHeight;

        //check both possible solutions
        float quadraticSolution = Math.abs((float) (( -b - Math.sqrt( b*b - 4*a*c ) ) / (2*a)));

        //convert answer back to seconds
        return quadraticSolution * 60.0f;
    }
}
