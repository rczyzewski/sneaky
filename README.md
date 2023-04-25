The original idea of this utility was inspired by lombok.
Together with vavr and lombok it can turn you java code upside down. 

```java
class PureJavaSample
{
    void transformInput()
    {

        new Scanner(System.in)
            .tokens()
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
            .forEach(it -> log.info("Our new string: {}", it));

    }
}
```

Can be written like: 
```java
class PureJavaSample
{

    void sneakyWrapExample()
    {
        new Scanner(System.in)
            .tokens()
              .map(SneakyWrap.wrap(it -> om.readValue(it, Integer.class)))
              .map(it -> it + 3)
              .map(SneakyWrap.wrap(om::writeValueAsString))
              .forEach(it -> log.info("Our new string: {}", it));
    }
}
```

