package com.skysoftatm.bblreactor.ch00.server;

import com.skysoftatm.bblreactor.ch00.domain.Altitude;
import com.skysoftatm.bblreactor.ch00.domain.Speed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

@RestController
class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    @GetMapping("/altitude/{id}")
    private Flux<Altitude> getAltitude(@PathVariable String id) {
        LOGGER.info("Requesting altitude for {}", id);
        return Flux.interval(ofSeconds(1)).map(Altitude::new).onBackpressureDrop();
    }

    @GetMapping("/speed/{id}")
    private Flux<Speed> getSpeed(@PathVariable String id) {
        LOGGER.info("Requesting speed for {}", id);
        return Flux.interval(ofMillis(10)).map(Speed::new).onBackpressureDrop();
    }
}
