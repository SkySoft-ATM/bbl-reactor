package com.skysoftatm.bblreactor.ch04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Threading {
    private static final Logger LOGGER = LoggerFactory.getLogger(Threading.class);


    public static void main(String[] args) {
        Flux<Long> source = Flux.create(sink -> {
            long i = 0;
                while(!sink.isCancelled()){
                    while(!sink.isCancelled() && sink.requestedFromDownstream() > 0){
                        sink.next(i);
                        LOGGER.info("Produced {}", i);
                        i++;
                        try {
                            SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
        });

        source
                .take(5)
                .subscribeOn(Schedulers.newSingle("FluxCreation")) // the work done in the Flux.create will be done in this thread
                .doOnNext(i -> LOGGER.info("first log "+i))
                .publishOn(Schedulers.newSingle("IntermediateThread")) // from this point, values are pushed on another thread
                .doOnNext(i -> LOGGER.info("second log "+i))
                .publishOn(Schedulers.newSingle("FluxSubscription")) // from this point, values are pushed on another thread
                .subscribe(i -> LOGGER.info("last log "+i));
    }
}
