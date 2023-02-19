package io.github.fourlastor.game.level.di;

import com.badlogic.ashley.core.ComponentMapper;
import dagger.Module;
import dagger.Provides;
import io.github.fourlastor.game.di.ScreenScoped;
import io.github.fourlastor.game.level.component.ActorComponent;
import io.github.fourlastor.game.level.component.AnimatedComponent;
import io.github.fourlastor.game.level.component.AreaComponent;
import io.github.fourlastor.game.level.component.BodyBuilderComponent;
import io.github.fourlastor.game.level.component.BodyComponent;
import io.github.fourlastor.game.level.component.InputComponent;
import io.github.fourlastor.game.level.component.MovingComponent;
import io.github.fourlastor.game.level.component.PlayerComponent;
import io.github.fourlastor.game.level.component.SoundComponent;
import io.github.fourlastor.game.level.entity.timedFloor.TimedFloorComponent;
import io.github.fourlastor.game.level.unphysics.component.GravityComponent;
import io.github.fourlastor.game.level.unphysics.component.KinematicBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.MovingBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SensorBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.SolidBodyComponent;
import io.github.fourlastor.game.level.unphysics.component.TransformComponent;

@Module
public class EcsModule {

    @Provides
    @ScreenScoped
    public ComponentMapper<AnimatedComponent> animatedComponent() {
        return ComponentMapper.getFor(AnimatedComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<InputComponent> inputComponent() {
        return ComponentMapper.getFor(InputComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<ActorComponent> actorComponent() {
        return ComponentMapper.getFor(ActorComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<BodyComponent> bodyComponent() {
        return ComponentMapper.getFor(BodyComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<BodyBuilderComponent> bodyBuilderComponent() {
        return ComponentMapper.getFor(BodyBuilderComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<PlayerComponent> playerComponent() {
        return ComponentMapper.getFor(PlayerComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<MovingComponent> movingComponent() {
        return ComponentMapper.getFor(MovingComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<SoundComponent> soundComponent() {
        return ComponentMapper.getFor(SoundComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<TimedFloorComponent> falseFloorComponent() {
        return ComponentMapper.getFor(TimedFloorComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<GravityComponent> gravityComponent() {
        return ComponentMapper.getFor(GravityComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<KinematicBodyComponent> kinematicBodyComponent() {
        return ComponentMapper.getFor(KinematicBodyComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<MovingBodyComponent> movingBodyComponent() {
        return ComponentMapper.getFor(MovingBodyComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<SolidBodyComponent> solidBodyComponent() {
        return ComponentMapper.getFor(SolidBodyComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<TransformComponent> transformComponent() {
        return ComponentMapper.getFor(TransformComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<SensorBodyComponent> sensorBodyComponent() {
        return ComponentMapper.getFor(SensorBodyComponent.class);
    }

    @Provides
    @ScreenScoped
    public ComponentMapper<AreaComponent> spikeComponent() {
        return ComponentMapper.getFor(AreaComponent.class);
    }
}
