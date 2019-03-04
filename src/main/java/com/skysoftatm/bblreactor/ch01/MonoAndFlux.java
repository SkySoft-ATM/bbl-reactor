package com.skysoftatm.bblreactor.ch01;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoAndFlux {

    public static void main(String[] args) {
        // Mono represents a single asynchronous value
        Mono<String> mono = Mono.just("someValue");

        //Flux represents a sequence of asynchronous values
        Flux<String> flux = Flux.just("a", "b", "c");

        mono.subscribe(System.out::println);
        flux.subscribe(System.out::println);
    }
}
