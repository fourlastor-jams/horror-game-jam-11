package io.github.fourlastor.game.level.input.state;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import io.github.fourlastor.game.level.Area;
import io.github.fourlastor.game.level.GameConfig;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import javax.inject.Inject;

public class OnLadder extends CharacterState {

    private final Vector2 originalGravity = new Vector2();
    private final GameConfig config;

    private final Vector2 velocity = new Vector2();

    @Inject
    public OnLadder(StateMappers mappers, GameConfig config) {
        super(mappers);
        this.config = config;
    }

    @Override
    protected String animation() {
        return "climb";
    }

    @Override
    public void enter(Entity entity) {
        super.enter(entity);
        GravityComponent gravityComponent = gravities.get(entity);
        originalGravity.set(gravityComponent.gravity);
        gravityComponent.gravity.set(0, 0);
        moving.get(entity).speed.x = 0f;
        moving.get(entity).speed.y = 0f;
    }

    @Override
    public void exit(Entity entity) {
        super.exit(entity);
        gravities.get(entity).gravity.set(originalGravity);
    }

    @Override
    public void update(Entity entity) {
        PlayerComponent player = players.get(entity);
        if (player.area != Area.LADDER) {
            player.stateMachine.changeState(player.fallingFromGround);
            return;
        }
        InputComponent input = inputs.get(entity);
        if (input.jumpJustPressed) {
            player.stateMachine.changeState(player.jumping);
            return;
        }
        velocity.set(0, 0);
        if (input.upPressed) {
            velocity.y += 1;
        }
        if (input.downPressed) {
            velocity.y -= 1;
        }
        if (input.leftPressed) {
            velocity.x -= 1;
        }
        if (input.rightPressed) {
            velocity.x += 1;
        }
        animated.get(entity).stateMachine.setAnimating(!velocity.equals(Vector2.Zero));
        moving.get(entity).speed.set(velocity.scl(config.player.ladderSpeed));
    }
}
