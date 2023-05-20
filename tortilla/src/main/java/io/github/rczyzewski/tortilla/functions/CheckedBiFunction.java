package io.github.rczyzewski.tortilla.functions;

@FunctionalInterface
public interface CheckedBiFunction<T, U, R> {
    R apply(T t, U u)
            throws Exception;
}
