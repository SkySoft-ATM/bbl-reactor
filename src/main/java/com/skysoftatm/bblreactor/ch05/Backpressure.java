package com.skysoftatm.bblreactor.ch05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Backpressure {

    private static final Logger LOGGER = LoggerFactory.getLogger(Backpressure.class);

    public static void main(String[] args) {
        Flux<Long> source = Flux.interval(Duration.ofMillis(1)) // this source does not respect backpressure
                .doOnNext(i -> System.out.println("Produced "+i))
                .doOnRequest(i -> System.out.println("Requested " + i));

        source
//                .onBackpressureDrop(i -> LOGGER.info("Dropped {} on backpressure", i))
//                .onBackpressureBuffer(10,
//                        i -> {
//                            LOGGER.info("Dropped {} on backpressure", i);
//                        },
//                        BufferOverflowStrategy.DROP_OLDEST)
                .publishOn(Schedulers.newSingle("SlowThread"), 10)
                .map(Backpressure::sleep)
                .doOnNext(i -> LOGGER.info("Received " + i))
                .blockLast();
    }


    private static Long sleep(Long i) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }
}
