The initial version of the code and the idea comes from my colleague.
Thank you, [Pablo](https://github.com/pablocrossa).
It has been inspired by [SneakyThrow](https://projectlombok.org/features/SneakyThrows). 

### Installation
For maven users:  
```xml
<dependency>
    <groupId>io.github.rczyzewski</groupId>
    <artifactId>tortilla</artifactId>
    <version>0.1.1-RC3</version>
</dependency>
```
snippets for other build systems are available [here](https://central.sonatype.com/artifact/io.github.rczyzewski/tortilla/)

### Example

It's useful while writing code with stream processing. It just looks better, in my humble opinion.

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
