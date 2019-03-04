package com.skysoftatm.bblreactor.ch03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HotAndColdStreams {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotAndColdStreams.class);

    /**
     * Reactive streams come in 2 flavors: hot and cold
     * - subscribing to a hot stream is like turning on the radio: you hear whatever is broadcasted now
     * - subscribing to a cold stream is like putting a CD in a reader: it starts from the beginning
     */
    public static void main(String[] args) throws InterruptedException {

        Flux<String> coldSource = Flux.just("a", "b", "c");

        coldSource.subscribe(i -> LOGGER.info("first " + i));
        coldSource.subscribe(i -> LOGGER.info("second " + i)); // they both see the same values

        Flux<Long> hotSource = Flux.interval(Duration.ofSeconds(1))
                .publish() // turns the cold stream into a hot stream
                .refCount(); // the first subscriber subscribes to the upstream source, the last one to disconnect closes the subscription
        Disposable sub1 = hotSource.subscribe(); // the first subscription will start the source

        SECONDS.sleep(3);

        hotSource
                .doOnNext(i -> LOGGER.info("hot: " + i))
                .take(5)
                .blockLast();

        sub1.dispose(); // this gets rid of the first subscription

    }
}
