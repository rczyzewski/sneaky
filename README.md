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
Currently, there are two functionalities provided by the library: 

 * [wrap](#wrap) -> forcing exceptions to behave like runtime exceptions
 * [lenses](#lenses) ->  working with immutable objects

#### wrap
It's useful while writing code with stream processing. It just looks better, in my humble opinion.

```java
    void sneakyWrapFlow(int limit)
    {
        Stream.generate(Tortilla.wrapCallable(FooClass::generate))
              .limit(limit)
              .map(Tortilla.wrap(it -> om.readValue(it, Integer.class)))
              .map(it -> it + 3)
              .map(Tortilla.wrap(om::writeValueAsString))
              .forEach(Tortilla.wrapConsumer(FooClass::consume));
    }
```

So it will look less like the below:
```java
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

```
A full example is available [here](https://github.com/rczyzewski/tortilla/blob/main/tortilla-examples/src/test/java/io/github/rczyzewski/tortilla/TortillaExampleTest.java).

#### Lenses
Let's assume whe have a Car defined as follows:

```java
    private final Car brokenCar = Car.builder()
        .id("foo")
        .engine(Engine.builder()
                .power(122.0f)
                .sparkPlug(SparkPlug.builder()
                        .producer("noname")
                        .shortCode("NN")
                        .state(SparkPlug.SparkPlugState.ELECTRODE_MELTED)
                        .build())
                .piston(Piston.builder()
                        .size(12.0f)
                        .operational(false)
                        .build())
                .build())
        .build();
```

As we see the car is broken: the piston is not operational and the spark plug is melted. 
In real life a mechanic will replace broken elements. In immutable world, we need to create a new object. 
With the support of this library, it might look like this: 

```java
Car repairedCar = Lens.focus(Car.Lenses.engine)
        .split(Lens.focus(Engine.Lenses.sparkPlug)
                .focus(SparkPlug.Lenses.state)
                .rebuildWith(SparkPlug.SparkPlugState.NORMAL))
        .focus(Engine.Lenses.piston)
        .focus(Piston.Lenses.operational)
        .rebuildWith(true)
        .apply(brokenCar);
```

Code snippets are taken from [here](https://github.com/rczyzewski/tortilla/blob/main/tortilla-examples/src/test/java/io/github/rczyzewski/tortilla/CarLensExampleTest.java).