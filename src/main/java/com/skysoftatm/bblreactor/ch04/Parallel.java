package com.skysoftatm.bblreactor.ch04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Parallel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Parallel.class);

    public static void main(String[] args) {

        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1)).take(10);

        ParallelFlux<String> parallel = interval.parallel(4)
                .runOn(Schedulers.elastic())
                .map(i -> {
                    LOGGER.info("Processing " + i);
                    if (i == 2) {
                        sleepSeconds(10);
                    }
                    return "value " + i;
                });

        parallel.sequential() // the original order is no longer guaranteed
                .doOnNext(LOGGER::info)
                .blockLast();

    }

    private static void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
