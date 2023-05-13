package io.github.rczyzewski.tortilla;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

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

    @Test
    void sneakyWrapFlow()
    {
        Stream.generate(Tortilla.wrapCallable(FooClass::generate))
              .limit(10)
              .map(Tortilla.wrap(it -> om.readValue(it, Integer.class)))
              .map(it -> it + 3)
              .map(Tortilla.wrap(om::writeValueAsString))
              .forEach(Tortilla.wrapConsumer(FooClass::consume));
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