package com.skysoftatm.bblreactor.ch01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Solution {

    private static final Logger LOGGER = LoggerFactory.getLogger(Solution.class);

    public static void main(String[] args) {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));  // this publishes on a daemon thread

        interval.doOnNext(i -> LOGGER.info(""+i))
                .take(10)   // we take only 10 values
                .blockLast(); // to prevent the JVM from exiting, we block till the last element
    }


}
