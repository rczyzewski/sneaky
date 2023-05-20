
package io.github.rczyzewski.tortilla;

import io.github.rczyzewski.tortilla.functions.CheckedBiFunction;
import io.github.rczyzewski.tortilla.functions.CheckedConsumer;
import io.github.rczyzewski.tortilla.functions.CheckedFunction;
import io.github.rczyzewski.tortilla.functions.CheckedRunnable;
import io.github.rczyzewski.tortilla.functions.CheckedSupplier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public final class SneakyTortilla {
    @SneakyThrows
    private static <T, U, R> R biFunctionWrap(CheckedBiFunction<T, U, R> function, T t, U u) {
        return function.apply(t, u);
    }

    @SneakyThrows
    private static <T, R> R functionWrap(CheckedFunction<T, R> function, T t) {
        return function.apply(t);
    }

    @SneakyThrows
    private static void runnableWrap(CheckedRunnable runnable) {
        runnable.run();
    }

    @SneakyThrows
    private static <T> void consumerWrap(CheckedConsumer<T> consumer, T t) {
        consumer.accept(t);
    }

    public static <T, U, R> BiFunction<T, U, R> wrap(CheckedBiFunction<T, U, R> function) {
        return (t, u) -> biFunctionWrap(function, t, u);
    }

    public static <T, R> Function<T, R> wrap(CheckedFunction<T, R> function) {
        return t -> functionWrap(function, t);
    }

    public static Runnable wrapRunnable(CheckedRunnable runnable) {
        return () -> runnableWrap(runnable);
    }

    @SneakyThrows
    private static <R> R supplierWrap(CheckedSupplier<R> supplier) {
        return supplier.get();
    }

    public static <R> Supplier<R> wrapCallable(Callable<R> callable) {
        return () -> supplierWrap(callable::call);
    }

    public static <R> Consumer<R> wrapConsumer(CheckedConsumer<R> consumer) {
        return it -> consumerWrap(consumer, it);
    }

}
