package io.github.rczyzewski.tortilla;

import io.github.rczyzewski.tortilla.lens.Lens;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.UtilityClass;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarLensExampleTest {

    private final Car brokenCar = Car.builder()
            .id("foo")
            .engine(Engine.builder()
                    .power(122.0f)
                    .sparkPlug(SparkPlug.builder()
                            .producer("noname")
                            .shortCode("NN")
                            .state(SparkPlug.SparkPlugState.ELECTRODE_MELTED)
                            .build())
                    .piston(Piston.builder()
                            .size(12.0f)
                            .operational(false)
                            .build())
                    .build())
            .build();

    private void verifyNotModified() {
        assertThat(brokenCar.getId()).isEqualTo("foo");
        assertThat(brokenCar.getEngine().getSparkPlug().getProducer()).isEqualTo("noname");
        assertThat(brokenCar.getEngine().getSparkPlug().getShortCode()).isEqualTo("NN");
        assertThat(brokenCar.getEngine().getSparkPlug().getState()).isEqualTo(SparkPlug.SparkPlugState.ELECTRODE_MELTED);
        assertThat(brokenCar.getEngine().getPower()).isEqualTo(122.0f, Offset.offset(0.001f));
        assertThat(brokenCar.getEngine().getPiston().getSize()).isEqualTo(12.0f, Offset.offset(0.001f));
        assertThat(brokenCar.getEngine().getPiston().isOperational()).isFalse();
    }

    @Test
    void engineExample1() {

        //We need to  change piston
        Car repairedCar = Lens.focus(Lens.of(Car::getEngine, Car::withEngine))
                .focus(Lens.of(Engine::getPiston, Engine::withPiston))
                .focus(Lens.of(Piston::isOperational, Piston::withOperational))
                .rebuildWith(true)
                .apply(brokenCar);

        assertThat(repairedCar.getId()).isEqualTo("foo");
        assertThat(repairedCar.getEngine().getPiston().isOperational()).isTrue();
        verifyNotModified();
    }

    @Test
    void engineExample2() {

        //How the code could look like, when Lenses are written
        Car repairedCar = Lens.focus(Car.Lenses.engine)
                .focus(Engine.Lenses.piston)
                .focus(Piston.Lenses.operational)
                .rebuildWith(true)
                .apply(brokenCar);

        assertThat(repairedCar.getId()).isEqualTo("foo");
        assertThat(repairedCar.getEngine().getPiston().isOperational()).isTrue();
        verifyNotModified();
    }

    @Test
    void engineExample3() {

        //We need to  change piston & spark plug
        Car repairedCar = Lens.focus(Car.Lenses.engine)
                .split(Lens.focus(Engine.Lenses.sparkPlug)
                        .focus(SparkPlug.Lenses.state)
                        .rebuildWith(SparkPlug.SparkPlugState.NORMAL))
                .focus(Engine.Lenses.piston)
                .focus(Piston.Lenses.operational)
                .rebuildWith(true)
                .apply(brokenCar);

        assertThat(repairedCar.getId()).isEqualTo("foo");
        assertThat(repairedCar.getEngine().getPiston().isOperational()).isTrue();
        assertThat(repairedCar.getEngine().getSparkPlug().getState()).isEqualTo(SparkPlug.SparkPlugState.NORMAL);
        verifyNotModified();
    }
    @Test
    void engineExampleWithoutUsingLensPattern() {
        Piston newPiston = brokenCar.getEngine()
                .getPiston()
                .withOperational(true);

        SparkPlug newSparkPlug = brokenCar.getEngine()
                .getSparkPlug()
                .withState(SparkPlug.SparkPlugState.NORMAL);

        Engine newEngine = brokenCar.getEngine()
                .withPiston(newPiston)
                .withSparkPlug(newSparkPlug);

        Car repairedCar = brokenCar.withEngine(newEngine);

        assertThat(repairedCar.getId()).isEqualTo("foo");
        assertThat(repairedCar.getEngine().getPiston().isOperational()).isTrue();
        assertThat(repairedCar.getEngine().getSparkPlug().getState()).isEqualTo(SparkPlug.SparkPlugState.NORMAL);
        verifyNotModified();
    }
}

@With
@Value
@Builder
class Car {
    String id;
    Engine engine;

    @UtilityClass
    public static class Lenses {
        static final Lens<Car, String> id = Lens.of(Car::getId, Car::withId);
        static final Lens<Car, Engine> engine = Lens.of(Car::getEngine, Car::withEngine);
    }
}

@With
@Value
@Builder
class Engine {

    Piston piston;
    SparkPlug sparkPlug;
    float power;

    @UtilityClass
    public static class Lenses {
        static final Lens<Engine, Piston> piston = Lens.of(Engine::getPiston, Engine::withPiston);
        static final Lens<Engine, SparkPlug> sparkPlug = Lens.of(Engine::getSparkPlug, Engine::withSparkPlug);
        static final Lens<Engine, Float> power = Lens.of(Engine::getPower, Engine::withPower);
    }
}

@With
@Value
@Builder
class SparkPlug {
    String producer;
    String shortCode;
    SparkPlugState state;

    enum SparkPlugState {
        NORMAL, ELECTRODE_MELTED, OILEDUP, SOOT_DEPOSIT
    }

    @UtilityClass
    public static class Lenses {
        static final Lens<SparkPlug, String> producer = Lens.of(SparkPlug::getProducer, SparkPlug::withProducer);
        static final Lens<SparkPlug, String> shortCode = Lens.of(SparkPlug::getShortCode, SparkPlug::withShortCode);
        static final Lens<SparkPlug, SparkPlugState> state = Lens.of(SparkPlug::getState, SparkPlug::withState);
    }
}

@With
@Value
@Builder
class Piston {
    float size;
    boolean operational;

    public static class Lenses {
        static final Lens<Piston, Float> size = Lens.of(Piston::getSize, Piston::withSize);
        static final Lens<Piston, Boolean> operational = Lens.of(Piston::isOperational, Piston::withOperational);
    }
}