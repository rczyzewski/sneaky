package io.github.rczyzewski.tortilla.functions;

@FunctionalInterface
public interface CheckedRunnable {
    void run()
            throws Exception;
}
