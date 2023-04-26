package io.github.rczyzewski.tortilla;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

@Slf4j
public class TortillaExampleTest
{

    private final static ObjectMapper om = new ObjectMapper();

    @Test
    void withoutSneakyWrapExample()
    {
        Stream.generate(() -> {
                  try {
                      return FooClass.generate();
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  }
              })
            .limit(10)
            .map(it -> {
                try {
                    return om.readValue(it, Integer.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            })
            .map(it -> it + 3)
            .map(it -> {
                try {
                    return om.writeValueAsString(it);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            })
            .forEach(s -> {
                try {
                    FooClass.consume(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
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

@Slf4j
class  FooClass {

     static String generate() throws IOException {
         return "1234";
     }
     static void consume(String s ) throws IOException {
         log.info("the string '{}' has been consumed", s);
     }

 }