package com.skysoftatm.bblreactor.ch01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Problem {

    private static final Logger LOGGER = LoggerFactory.getLogger(Problem.class);

    public static void main(String[] args) {
        Flux<String> source = Flux.just("one", "two", "three");
        source.subscribe(LOGGER::info);  // this call to subscribe works

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
        interval
                .take(10)
                .subscribe(i -> LOGGER.info(""+i)); // this one does not seem to do anything


    }


}
