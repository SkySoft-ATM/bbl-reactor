package com.skysoftatm.bblreactor.ch02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Signals {
    private static final Logger LOGGER = LoggerFactory.getLogger(Signals.class);

    /**
     * 3 types of signals are emitted on reactive streams
     * - onNext
     * - onError
     * - onComplete
     *
     * onError and onComplete are terminal events: they appear at most once per subscription
     */
    public static void main(String[] args) {

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        interval
                .take(5)
                //.map(Signals::errorIfThree) // introduces an error
                .doOnNext(i -> LOGGER.info("onNext {}", i))
                .doOnError(err -> LOGGER.info("onError", err))
                .doOnComplete(() -> LOGGER.info("Stream completed"))
                .blockLast();

    }

    private static Long errorIfThree(Long i) {
        if(i == 3){
            throw new RuntimeException("simulate an exception");
        }
        return i;
    }
}
