package io.github.rczyzewski.tortilla.functions;

@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t)
            throws Exception;
}
