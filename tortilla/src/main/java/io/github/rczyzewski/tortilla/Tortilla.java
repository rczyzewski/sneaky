
package io.github.rczyzewski.tortilla;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public final class Tortilla
{
    @SneakyThrows
    private static <T, U, R> R biFunctionWrap(CheckedBiFunction<T, U, R> checkedBiFunction, T t, U u)
    {
        return checkedBiFunction.apply(t, u);
    }

    @SneakyThrows
    private static <T, R> R functionWrap(CheckedFunction<T, R> checkedFunction, T t)
    {
        return checkedFunction.apply(t);
    }

    @SneakyThrows
    private static void runnableWrap(CheckedRunnable checkedRunnable)
    {
        checkedRunnable.run();
    }

    @SneakyThrows
    private static <R> R supplierWrap(CheckedSupplier<R> checkedSupplier)
    {
        return checkedSupplier.get();
    }

    @SneakyThrows
    private static <T> void consumerWrap(CheckedConsumer<T> consumer, T t)
    {
        consumer.accept(t);
    }

    public static <T, U, R> BiFunction<T, U, R> wrap(CheckedBiFunction<T, U, R> checkedFunction)
    {
        return (t, u) -> biFunctionWrap(checkedFunction, t, u);
    }

    public static <T, R> Function<T, R> wrap(CheckedFunction<T, R> checkedFunction)
    {
        return t -> functionWrap(checkedFunction, t);
    }

    public static Runnable wrapRunnable(CheckedRunnable checkedRunnable)
    {
        return () -> runnableWrap(checkedRunnable);
    }

    public static <R> Supplier<R> wrapCallable(Callable<R> callable)
    {
        return () -> supplierWrap(callable::call);
    }

    public static <R> Consumer<R> wrapConsumer(CheckedConsumer<R> consumer)
    {
        return it -> consumerWrap(consumer, it);
    }

    @FunctionalInterface
    public interface CheckedBiFunction<T, U, R>
    {
        R apply(T t, U u)
            throws Exception;
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R>
    {
        R apply(T t)
            throws Exception;
    }

    @FunctionalInterface
    public interface CheckedRunnable
    {
        void run()
            throws Exception;
    }

    @FunctionalInterface
    public interface CheckedConsumer<T>
    {
        void accept(T t)
            throws Exception;
    }

    @FunctionalInterface
    public interface CheckedSupplier<R>
    {
        R get()
            throws Exception;
    }
}
