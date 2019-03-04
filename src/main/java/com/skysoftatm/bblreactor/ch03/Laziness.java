package com.skysoftatm.bblreactor.ch03;

import reactor.core.publisher.Mono;

public class Laziness {

    public static void main(String[] args) {
        // this defer statement will only be executed on subscription
        Mono<String> source = Mono.defer(() -> {
            System.out.println("\t\t-> I'm actually doing something");
            return Mono.just("aValue");
        });

        System.out.println("Starting first subscription");
        source.subscribe(i -> System.out.println("received " + i));

        System.out.println("\nStarting second subscription");
        source.subscribe(i -> System.out.println("received " + i));

        System.out.println("=====================");

        //what if we want to call the defered code only once?

        Mono<String> cached = source.cache();

        System.out.println("Starting first cached subscription");
        cached.subscribe(i -> System.out.println("received " + i));

        System.out.println("\nStarting second cached subscription");
        cached.subscribe(i -> System.out.println("received " + i)); // this subscription takes the cached value
    }
}
