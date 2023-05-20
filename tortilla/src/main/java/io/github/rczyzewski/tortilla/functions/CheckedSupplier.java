package io.github.rczyzewski.tortilla.functions;

@FunctionalInterface
public interface CheckedSupplier<R> {
    R get() throws Exception;
}
