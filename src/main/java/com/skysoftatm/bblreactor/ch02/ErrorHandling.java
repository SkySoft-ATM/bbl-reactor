package com.skysoftatm.bblreactor.ch02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ErrorHandling {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandling.class);

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

                .map(ErrorHandling::errorIfThree) // introduces an error
//                .retry() // retries indefinitely without delay
//                .retryBackoff(3, Duration.ofSeconds(1), Duration.ofSeconds(5))
//                .onErrorReturn(999L) // return a default value and ends the stream
//                .onErrorContinue((err, value) -> LOGGER.info("Error happened with value {} : {}", value, err.getCause()))
//                .onErrorResume(err -> Flux.just(8L,9L,10L)) // subscribes to a backup stream
                .doOnNext(i -> LOGGER.info("onNext {}", i))
                .doOnError(err -> LOGGER.info("onError", err))
                .doOnComplete(() -> LOGGER.info("Stream completed"))
                .take(10)
                .blockLast();

    }

    private static Long errorIfThree(Long i) {
        if(i == 3){
            throw new RuntimeException("simulate an exception");
        }
        return i;
    }
}
