package com.skysoftatm.bblreactor.ch00.client;

import com.skysoftatm.bblreactor.ch00.domain.Altitude;
import com.skysoftatm.bblreactor.ch00.domain.Speed;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

public class Client {
    private static final String FLIGHT = "AF1234";
    private static final WebClient WEB_CLIENT = WebClient.create("http://localhost:8080");

    public static void main(String[] args) {
        Flux<Speed> speedFlux = getSpeed(FLIGHT);
        Flux<Altitude> altitudeFlux = getAltitude(FLIGHT);
        Flux<String> infoFlux = Flux.combineLatest(Client::combineValues, speedFlux, altitudeFlux);

        infoFlux.doOnNext(System.out::println).blockLast();
    }

    private static String combineValues(Object[] values) {
        return "Info for Flight " + FLIGHT + " " + values[0] + " " + values[1];
    }

    private static Flux<Speed> getSpeed(String id) {
        return WEB_CLIENT.get()
                .uri("/speed/{id}", id)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .flatMapMany(cr -> cr.bodyToFlux(Speed.class));
    }

    private static Flux<Altitude> getAltitude(String id) {
        return WEB_CLIENT
                .get()
                .uri("/altitude/{id}", id)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .flatMapMany(cr -> cr.bodyToFlux(Altitude.class));
    }












//    .buffer(100)
//                .map(l -> l.stream().mapToDouble(Speed::getSpeed).average())
//            .map(s -> new Speed(s.getAsDouble()));


    // altitudeFlux.withLatestFrom(speedFlux, (a, s) -> a + " " + s).doOnNext(System.out::println).blockLast();
}
