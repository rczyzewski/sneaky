
package io.github.rczyzewski.tortilla;

import io.github.rczyzewski.tortilla.functions.CheckedBiFunction;
import io.github.rczyzewski.tortilla.functions.CheckedConsumer;
import io.github.rczyzewski.tortilla.functions.CheckedFunction;
import io.github.rczyzewski.tortilla.functions.CheckedRunnable;
import io.github.rczyzewski.tortilla.functions.CheckedSupplier;
import lombok.experimental.StandardException;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public final class Tortilla {

    @StandardException
    public static class Spoiled extends RuntimeException {

    }

    public static <T, U, R> BiFunction<T, U, R> wrap(CheckedBiFunction<T, U, R> function) {
        return (t, u) -> {
            try {
                return function.apply(t, u);
            } catch (Exception e) {
                throw new Spoiled(e);
            }
        };
    }

    public static <T, R> Function<T, R> wrap(CheckedFunction<T, R> function) {
        return t ->
        {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new Spoiled(e);
            }

        };
    }

    public static Runnable wrapRunnable(CheckedRunnable runnable) {
        return () ->
        {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new Spoiled(e);
            }
        };
    }

    private static <R> Supplier<R> supplierWrap(CheckedSupplier<R> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new Spoiled(e);
            }
        };

    }

    public static <R> Supplier<R> wrapCallable(Callable<R> callable) {
        return supplierWrap(callable::call);

    }

    public static <R> Consumer<R> wrapConsumer(CheckedConsumer<R> consumer) {
        return it -> {
            try {
                consumer.accept(it);
            } catch (Exception e) {
                throw new Spoiled(e);
            }
        };
    }

}
