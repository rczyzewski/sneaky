The original idea of this utility was inspired by lombok.
Together with vavr and lombok it can turn you java code upside down, so it might look more like this:
```java
class SneakyWrapSample
{
    void sneakyWrapFlow(int limit)
    {
        Stream.generate(Tortilla.wrapCallable(FooClass::generate))
              .limit(limit)
              .map(Tortilla.wrap(it -> om.readValue(it, Integer.class)))
              .map(it -> it + 3)
              .map(Tortilla.wrap(om::writeValueAsString))
              .forEach(Tortilla.wrapConsumer(FooClass::consume));
    }
}
```

So it will look less like the below:
```java
class PureJavaSample
{
    void withoutSneakyWrapExample(int limit)
    {
        Stream.generate(() -> {
                  try {
                      return FooClass.generate();
                  } catch (FancyCheckedException e) {
                      throw new FancyRuntimeException(e);
                  }
              })
              .limit(limit)
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
}

class FancyRuntimeException extends RuntimeException
{
    FancyRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
```
A full example is available [here](https://github.com/rczyzewski/tortilla/blob/main/examples/src/test/java/io/github/rczyzewski/tortilla/TortillaExampleTest.java).