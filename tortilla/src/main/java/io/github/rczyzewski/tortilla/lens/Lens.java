package io.github.rczyzewski.tortilla.lens;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.BiFunction;
import java.util.function.Function;

@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class Lens<O, I> {
    @NonNull Function<O, I> get;
    @NonNull BiFunction<O, I, O> rebuild;

    static <O, I> Lens.Microscope<O, I> focus(Lens<O, I> lens) {
        return Lens.Microscope.<O, I>builder().lens(lens).build();
    }

    @Builder
    public static class Microscope<R, I> {
        Lens<R, I> lens;

        public Function<R,R> witherIt(I value) {
            return it-> lens.getRebuild().apply(it, value);
        }

        public <T> Microscope<R, T> focus(Lens<I, T> newLens) {

            BiFunction<R, T, R> rewriteFunction = (root, value) -> {
                I innerObject = lens.get.apply(root);
                I futureInnerObject = newLens.rebuild.apply(innerObject, value);
               return  lens.rebuild.apply(root, futureInnerObject);
            };

            return Lens.Microscope.<R, T>builder()
                    .lens(Lens.<R, T>builder()
                            .get(this.lens.get.andThen(newLens.get))
                            .rebuild(rewriteFunction)
                            .build())
                    .build();
        }
    }
}

