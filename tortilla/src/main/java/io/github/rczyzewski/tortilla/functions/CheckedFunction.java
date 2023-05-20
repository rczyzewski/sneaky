package io.github.rczyzewski.tortilla.functions;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t)
            throws Exception;
}
