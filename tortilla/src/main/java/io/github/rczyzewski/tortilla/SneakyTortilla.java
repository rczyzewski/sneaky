
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

/***
 *  The aim of this class is to provide a SneakyThrow behavior, for a lambda calls.
 *  In the background it uses a SneakyThrows annotation from
 *  <a href="https://projectlombok.org/features/SneakyThrows">lombok</a>
 *  project.
 *  <br>
 *  Examples of
 *  <a href="https://github.com/rczyzewski/tortilla/blob/main/tortilla-examples/src/test/java/io/github/rczyzewski/tortilla/TortillaExampleTest.java">usage</a>.
 */
@UtilityClass
public final class SneakyTortilla{
    @SneakyThrows
    private static <T, U, R> R biFunctionWrap(CheckedBiFunction<T, U, R> function, T t, U u){
        return function.apply(t, u);
    }

    @SneakyThrows
    private static <T, R> R functionWrap(CheckedFunction<T, R> function, T t){
        return function.apply(t);
    }

    @SneakyThrows
    private static void runnableWrap(CheckedRunnable runnable){
        runnable.run();
    }

    @SneakyThrows
    private static <T> void consumerWrap(CheckedConsumer<T> consumer, T t){
        consumer.accept(t);
    }

    @SneakyThrows
    private static <R> R supplierWrap(CheckedSupplier<R> supplier){
        return supplier.get();
    }

    /**
     * Can be used to create a "pure" function, from a function that throws a checked exception.
     * Exception will still be thrown, but it won't be checked during a compilation time - it will effectively
     * behave like a RuntimeException
     *
     * @param function the function that might throw exception
     */
    public static <T, U, R> BiFunction<T, U, R> wrap(CheckedBiFunction<T, U, R> function){
        return (t, u) -> biFunctionWrap(function, t, u);
    }

    /**
     * Can be used to create a "pure" function, from a function that throws a checked exception.
     * Exception will still be thrown, but it won't be checked during a compilation time - it will effectively
     * behave like a RuntimeException
     *
     * @param function the function that might throw exception
     */
    public static <T, R> Function<T, R> wrap(CheckedFunction<T, R> function){
        return t -> functionWrap(function, t);
    }

    /**
     * Can be used to create a "pure" call, from a function that throws a checked exception.
     * Exception will still be thrown, but it won't be checked during a compilation time - it will effectively
     * behave like a RuntimeException
     *
     * @param runnable almost correct Runnable, but might throw exception
     */
    public static Runnable wrapRunnable(CheckedRunnable runnable){
        return () -> runnableWrap(runnable);
    }


    /**
     * Can be used to create a "pure" call, from a function that throws a checked exception.
     * Exception will still be thrown, but it won't be checked during a compilation time - it will effectively
     * behave like a RuntimeException
     *
     * @param callable almost correct Runnable, but might throw exception
     */
    public static <R> Supplier<R> wrapCallable(Callable<R> callable){
        return () -> supplierWrap(callable::call);
    }

    /**
     * Can be used to create a "pure" call, from a function that throws a checked exception.
     * Exception will still be thrown, but it won't be checked during a compilation time - it will effectively
     * behave like a RuntimeException
     *
     * @param consumer almost right Consumer, but might throw exception
     */
    public static <R> Consumer<R> wrapConsumer(CheckedConsumer<R> consumer){
        return it -> consumerWrap(consumer, it);
    }

}
