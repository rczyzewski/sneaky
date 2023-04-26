package io.github.rczyzewski.tortilla;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@Slf4j
public class TortillaTests
{

    @SneakyThrows
    @Test
    public void wrapTest()
    {
        Tortilla.CheckedFunction<String, String> function = Mockito.mock(CheckedStringFunction.class);
        Mockito.when(function.apply(Mockito.anyString())).thenReturn("FOO").thenThrow(new IOException());

        Function<String, String> wrapped = Tortilla.wrap(function);

        assertThat(wrapped.apply("foo")).isEqualTo("FOO");

        assertThatThrownBy(() -> wrapped.apply("bar"))
            .isInstanceOf(IOException.class);

    }

    @Test
    @SneakyThrows
    public void biWrapTest()
    {
        Tortilla.CheckedBiFunction<String, String, String> function = Mockito.mock(CheckedStringBiFunction.class);
        Mockito.when(function.apply(Mockito.anyString(), Mockito.anyString())).thenReturn("ABC").thenThrow(
            new IOException());

        BiFunction<String, String, String> wrapped = Tortilla.wrap(function);

        assertThat(wrapped.apply("foo", "bar")).isEqualTo("ABC");

        assertThatThrownBy(() -> wrapped.apply("foo", "bar"))
            .isInstanceOf(IOException.class);

    }

    @Test
    @SneakyThrows
    public void runnableWrapHappyDayTest()
    {
        Tortilla.CheckedRunnable checkedRunnable = Mockito.mock(Tortilla.CheckedRunnable.class);
        Mockito.doNothing().when(checkedRunnable).run();
        Runnable wrapped = Tortilla.wrapRunnable(checkedRunnable);
        wrapped.run();
        Mockito.verify(checkedRunnable, times(1)).run();
    }

    @Test
    @SneakyThrows
    public void runnableWrapRainyDayTest()
    {
        Tortilla.CheckedRunnable checkedRunnable = Mockito.mock(Tortilla.CheckedRunnable.class);
        Mockito.doThrow(IOException.class).when(checkedRunnable).run();
        Runnable wrapped = Tortilla.wrapRunnable(checkedRunnable);
        assertThatThrownBy(wrapped::run).isInstanceOf(IOException.class);

    }

    @Test
    @SneakyThrows
    public void callableTest()
    {
        Callable<String> callable = Mockito.mock(StringCallable.class);
        Mockito.when(callable.call()).thenReturn("ABC").thenThrow(new IOException());

        Supplier<String> wrapped = Tortilla.wrapCallable(callable);

        assertThat(wrapped.get()).isEqualTo("ABC");

        assertThatThrownBy(wrapped::get)
            .isInstanceOf(IOException.class);
    }

    @Test
    @SneakyThrows
    public void consumerSunnyDayTest()
    {
        Tortilla.CheckedConsumer<String> consumer = Mockito.mock(CheckedStringConsumer.class);

        Mockito.doNothing().when(consumer).accept(anyString());

        Consumer<String> wrapped = Tortilla.wrapConsumer(consumer);

        wrapped.accept("foo");
        Mockito.verify(consumer, times(1)).accept(anyString());

    }

    @Test
    @SneakyThrows
    public void consumerRainyDayTest()
    {
        Tortilla.CheckedConsumer<String> consumer = Mockito.mock(CheckedStringConsumer.class);

        Mockito.doThrow(IOException.class).when(consumer).accept(anyString());

        Consumer<String> wrapped = Tortilla.wrapConsumer(consumer);
        assertThatThrownBy(() -> wrapped.accept("foo")).isInstanceOf(IOException.class);

    }

    private interface CheckedStringFunction extends Tortilla.CheckedFunction<String, String> {}

    private interface CheckedStringBiFunction extends Tortilla.CheckedBiFunction<String, String, String> {}

    private interface StringCallable extends Callable<String> {}

    private interface CheckedStringConsumer extends Tortilla.CheckedConsumer<String> {}
}