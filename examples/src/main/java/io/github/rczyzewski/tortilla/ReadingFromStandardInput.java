package io.github.rczyzewski.tortilla;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;

public class ReadingFromStandardInput
{

    private final static ObjectMapper om = new ObjectMapper();

    void sneakyWrapExample()
    {
        new Scanner(System.in)
            .tokens()
            .map(Tortilla.wrap(it -> om.readValue(it, Integer.class)))
            .map(it -> it + 3)
            .map(Tortilla.wrap(om::writeValueAsBytes))
            .forEach(Tortilla.consumerWrap(System.out::println));
    }

}
