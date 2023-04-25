package io.github.rczyzewski.tortilla;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class TortillaTests
{

    @Test
    public void dddTest(){

        List.of(1)
            .stream()
                .map( Tortilla.wrap( it -> Optional.of(it).filter(d-> d > 100).orElseThrow()) )
            .sorted()
            .count();
    }
}