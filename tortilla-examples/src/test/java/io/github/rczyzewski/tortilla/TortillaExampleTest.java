package io.github.rczyzewski.tortilla;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
import static io.github.rczyzewski.tortilla.SneakyTortilla.*;

@Slf4j
class TortillaExampleTest
{

    private final static ObjectMapper om = new ObjectMapper();

    @Test
    void withoutSneakyWrapExample()
    {
        Stream.generate(() -> {
                  try {
                      return FooClass.generate();
                  } catch (FancyCheckedException e) {
                      throw new FancyRuntimeException(e);
                  }
              })
              .limit(10)
              .map(it -> {
                  try {
                      return om.readValue(it, Integer.class);
                  } catch (JsonProcessingException e) {
                      throw new FancyRuntimeException(e);
                  }
              })
              .map(it -> it + 3)
              .map(it -> {
                  try {
                      return om.writeValueAsString(it);
                  } catch (JsonProcessingException e) {
                      throw new FancyRuntimeException(e);
                  }
              })
              .forEach(s -> {
                  try {
                      FooClass.consume(s);
                  } catch (FancyCheckedException e) {
                      throw new FancyRuntimeException(e);
                  }
              });

    }

    /**
     * This is an example, how Tortilla project might make your life easier:
     *
     * FooClass::generate might throw CheckedException -> and it won't be accepted as a parm of Stream::generate
     * om.readValue ->  throwing exception, can't be put directly as a param for Stream::map
     * om::writeValueAsString is also throwing exception, can't be put directly as a param for Stream::map
     * FooClass:consume throws a checked exception, compiler won't allow to pass it directly to forEach
     */
    @Test
    void sneakyWrapFlow()
    {
        Stream.generate(wrapCallable(FooClass::generate))
              .limit(10)
              .map(wrap(it -> om.readValue(it, Integer.class)))
              .map(it -> it + 3)
              .map(wrap(om::writeValueAsString))
              .forEach(wrapConsumer(FooClass::consume));
    }
}

class FooClass
{

    static String generate()
        throws FancyCheckedException
    {
        return "1234";
    }

    static void consume(String s)
        throws FancyCheckedException
    {
    }
}

class FancyCheckedException extends Exception {}

class FancyRuntimeException extends RuntimeException
{
    FancyRuntimeException(Throwable cause)
    {
        super(cause);
    }

}