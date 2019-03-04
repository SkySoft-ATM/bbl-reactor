package com.skysoftatm.bblreactor.ch02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SignalsContinued {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignalsContinued.class);

    /**
     * We can pass lambdas to the subscribe method to handle each signal
     */
    public static void main(String[] args) throws InterruptedException {

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        interval
                .take(5)
                .subscribe(i -> LOGGER.info("onNext {}", i),
                        err -> LOGGER.info("onError", err),
                        () -> LOGGER.info("Stream completed"));

        SECONDS.sleep(7); // necessary to keep the JVM alive.

    }
}
